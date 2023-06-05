package ucb.judge.ujsubmissions.bl

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ucb.judge.ujsubmissions.amqp.producer.SubmissionProducer
import ucb.judge.ujsubmissions.dao.*
import ucb.judge.ujsubmissions.dao.repository.LanguageRepository
import ucb.judge.ujsubmissions.dao.repository.SubmissionRepository
import ucb.judge.ujsubmissions.dto.*
import ucb.judge.ujsubmissions.exception.UjBadRequestException
import ucb.judge.ujsubmissions.exception.UjForbiddenException
import ucb.judge.ujsubmissions.exception.UjNotFoundException
import ucb.judge.ujsubmissions.service.*
import ucb.judge.ujsubmissions.utils.FileUtils
import ucb.judge.ujsubmissions.utils.KeycloakSecurityContextHolder
import java.sql.Timestamp
import java.util.*

@Service
class SubmissionBl constructor(
    private val ujFileUploaderService: UjFileUploaderService,
    private val ujProblemsService: UjProblemsService,
    private val ujUsersService: UjUsersService,
    private val ujContestService: UjContestService,
    private val minioService: MinioService,
    private val keycloakBl: KeycloakBl,
    private val languageRepository: LanguageRepository,
    private val submissionRepository: SubmissionRepository,
    private val submissionProducer: SubmissionProducer
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(SubmissionBl::class.java)
    }

    /**
     * Business logic to create a submission. This method will create a submission with the given data.
     * @param submission: Data of the submission.
     * @return Long: id of the created submission.
     */
    fun createSubmission(submission: NewSubmissionDto): Long {
        val token = "Bearer ${keycloakBl.getToken()}"
        // validate problem
        if(!ujProblemsService.problemExists(submission.problemId, token).data!!) {
            throw UjNotFoundException("Problem does not exist")
        }
        val problem = Problem()
        problem.problemId = submission.problemId

        // validate language
        val language: Language = languageRepository.findByLanguageIdAndStatusIsTrue(submission.languageId)
            ?: throw UjNotFoundException("Language not found")
        val admittedLanguages: List<Long> = ujProblemsService.getAllAdmittedLanguages(submission.problemId, token).data!!.map {
            it.languageId
        }
        if(!admittedLanguages.contains(language.languageId)) {
            throw UjBadRequestException("Language not admitted")
        }

        // validate student
        val kcUuid = KeycloakSecurityContextHolder.getSubject() ?: throw UjNotFoundException("Student not found")
        val studentId = ujUsersService.getStudentByKcUuid(kcUuid, token).data!!
        val student = Student()
        student.studentId = studentId

        // validate contest
        if(submission.contestId != null) {
            logger.info("Retrieving contest ${submission.contestId}")
            val studentSubject = KeycloakSecurityContextHolder.getSubject() ?: throw UjNotFoundException("Student not found")
            val contest = ujContestService.validateContestSubmission(submission.contestId, token, studentSubject, submission.problemId)

            logger.info("Contest ${submission.contestId} retrieved")
        }

        // upload file to minio
        val submissionFile = FileUtils.createSubmissionFile(submission.sourceCode, language.extension)
        val fileMetadata = ujFileUploaderService.uploadFile(submissionFile, "submissions", token)
        val s3Object = S3Object()
        s3Object.s3ObjectId = fileMetadata.data!!.s3ObjectId

        // store in database
        val contestProblemEntity = ContestProblem()
        contestProblemEntity.problem = problem
        if(submission.contestId != null) {
            val contest = Contest()
            contest.contestId = submission.contestId
            contestProblemEntity.contest = contest
        }
        contestProblemEntity.status = true

        val submissionEntity = Submission()
        submissionEntity.contestProblem = contestProblemEntity
        submissionEntity.language = language
        submissionEntity.student = student
        submissionEntity.s3SourceCode = s3Object
        submissionEntity.status = true
        submissionEntity.submissionDate = Timestamp(Date().time)

        val savedSubmission = submissionRepository.save(submissionEntity)

        val submissionInfoDto = SubmissionInfoDto(
            submissionId = savedSubmission.submissionId,
            problemId = savedSubmission.contestProblem!!.problem!!.problemId,
            languageId = savedSubmission.language!!.languageId,
        )
        submissionProducer.sendSubmission(submissionFile, submissionInfoDto)

        return savedSubmission.submissionId
    }

    /**
     * Business Logic to get a submission by its id.
     * @param submissionId: id of the submission to get.
     * @return SubmissionDto: submission with the given id.
     * @throws UjNotFoundException: if the submission does not exist.
     */
    fun getSubmissionById(submissionId: Long): SubmissionDto {
        logger.info("Getting submission with id $submissionId")
        val submission = submissionRepository.findBySubmissionIdAndStatusIsTrue(submissionId)
            ?: throw UjNotFoundException("Submission not found")
        val contest = submission.contestProblem!!.contest
        if(contest != null) {
            logger.info("Submission $submissionId is from contest ${contest.contestId}")
            // check if the professor is the owner of the contest
            // TODO: call uj-contests to validate
        }
        // check if the student is the owner of the submission
        val kcUuid = KeycloakSecurityContextHolder.getSubject() ?: throw UjBadRequestException("Invalid token")
        if(kcUuid != submission.student!!.kcUuid) {
            logger.warn("Student $kcUuid is not the owner of submission $submissionId")
            throw UjForbiddenException("You are not allowed to see this submission")
        }

        // generate keycloak token
        val token = "Bearer ${keycloakBl.getToken()}"

        val submissionDto = SubmissionDto()
        submissionDto.submissionId = submission.submissionId
        // call uj-problems to get problem info
        logger.info("Getting problem ${submission.contestProblem!!.problem!!.problemId} from uj-problems")
        val problem = ujProblemsService.getProblemMinimal(submission.contestProblem!!.problem!!.problemId, token).data!!
        submissionDto.problem = problem

        submissionDto.language = LanguageDto(
            languageId = submission.language!!.languageId,
            extension = submission.language!!.extension,
            name = submission.language!!.name,
        )
        submissionDto.sourceCode = String(minioService.getFile(submission.s3SourceCode!!.bucket, submission.s3SourceCode!!.filename))
        if(submission.verdictType != null) {
            submissionDto.verdict = VerdictTypeDto(
                verdictTypeId = submission.verdictType!!.verdictTypeId,
                name = submission.verdictType!!.description,
            )
        }
        submissionDto.submissionDate = submission.submissionDate!!
        // get testcases
        logger.info("Getting testcases from submission $submissionId")
        val problemTestcases = ujProblemsService.getProblemTestcases(submission.contestProblem!!.problem!!.problemId, token).data!!
        // create map from problemTestcases by id
        val problemTestcasesMap = mapOf(
            *problemTestcases.map { testcase -> testcase.testcaseId to testcase }.toTypedArray()
        )

        val testcaseList = mutableListOf<TestcaseSubmissionDto>()
        submission.testcaseSubmissions!!.forEach { testcaseSubmission ->
            val testcaseDto = TestcaseSubmissionDto(
                testcaseId = testcaseSubmission.testcaseSubmissionId,
                input = problemTestcasesMap[testcaseSubmission.testcase!!.testcaseId]!!.input,
                output = String(minioService.getFile(testcaseSubmission.s3Output!!.bucket, testcaseSubmission.s3Output!!.filename)),
                verdict = VerdictTypeDto(
                    verdictTypeId = testcaseSubmission.verdictType!!.verdictTypeId,
                    name = testcaseSubmission.verdictType!!.description,
                ),
                time = testcaseSubmission.time,
                memory = testcaseSubmission.memory,
            )
            testcaseList.add(testcaseDto)
        }
        submissionDto.testcases = testcaseList
        return submissionDto
    }

    /**
     * Business logic to get a submission's status by its id.
     */
fun getSubmissionStatus(submissionId: Long): SubmissionStatusDto {
        logger.info("Getting status of submission $submissionId")
        val submission = submissionRepository.findBySubmissionIdAndStatusIsTrue(submissionId)
            ?: throw UjNotFoundException("Submission not found")
        // check if the student is the owner of the submission
        logger.info("token ${KeycloakSecurityContextHolder.getSubject()}")
        val kcUuid = KeycloakSecurityContextHolder.getSubject() ?: throw UjBadRequestException("Invalid token")
        if(kcUuid != submission.student!!.kcUuid) {
            logger.warn("Student $kcUuid is not the owner of submission $submissionId")
            throw UjForbiddenException("You are not allowed to see this submission")
        }

        val testcaseCount = submission.contestProblem!!.problem!!.testcases!!.size
        val testcaseSubmissions = submission.testcaseSubmissions!!
        val submissionStatusList = mutableListOf<TestcaseStatusDto>()
        testcaseSubmissions.forEach { testcaseSubmission ->
            submissionStatusList.add(
                TestcaseStatusDto(
                    testcaseId = testcaseSubmission.testcase!!.testcaseId,
                    verdictType = VerdictTypeDto(
                        verdictTypeId = testcaseSubmission.verdictType!!.verdictTypeId,
                        name = testcaseSubmission.verdictType!!.description,
                    ),
                    time = testcaseSubmission.time,
                    memory = testcaseSubmission.memory,
                )
            )
        }
        // check if the submission is finished
        val finished = testcaseSubmissions.size == testcaseCount || submission.verdictType != null
        logger.info("Submission $submissionId is finished: $finished")
        return SubmissionStatusDto(
            submissionId = submission.submissionId,
            isDone = finished,
            testcases = submissionStatusList,
            testcaseCount = testcaseCount
        )
    }
}
