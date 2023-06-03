package ucb.judge.ujsubmissions.bl

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ucb.judge.ujsubmissions.amqp.producer.SubmissionProducer
import ucb.judge.ujsubmissions.dao.*
import ucb.judge.ujsubmissions.dao.repository.LanguageRepository
import ucb.judge.ujsubmissions.dao.repository.SubmissionRepository
import ucb.judge.ujsubmissions.dto.*
import ucb.judge.ujsubmissions.exception.UjNotFoundException
import ucb.judge.ujsubmissions.service.MinioService
import ucb.judge.ujsubmissions.service.UjFileUploaderService
import ucb.judge.ujsubmissions.service.UjProblemsService
import ucb.judge.ujsubmissions.service.UjUsersService
import ucb.judge.ujsubmissions.utils.FileUtils
import ucb.judge.ujsubmissions.utils.KeycloakSecurityContextHolder
import java.sql.Timestamp
import java.util.*

@Service
class SubmissionBl constructor(
    private val ujFileUploaderService: UjFileUploaderService,
    private val ujProblemsService: UjProblemsService,
    private val ujUsersService: UjUsersService,
    private val minioService: MinioService,
    private val keycloakBl: KeycloakBl,
    private val languageRepository: LanguageRepository,
    private val submissionRepository: SubmissionRepository,
    private val submissionProducer: SubmissionProducer
) {

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
        // TODO: validate language is within admitted languages for problem

        // validate student
        val kcUuid = KeycloakSecurityContextHolder.getSubject() ?: throw UjNotFoundException("Student not found")
        val studentId = ujUsersService.getStudentByKcUuid(kcUuid, token).data!!
        val student = Student()
        student.studentId = studentId

        // validate contest
        if(submission.contestId != null) {
            // TODO: validate with uj-contests
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
        val submission = submissionRepository.findBySubmissionIdAndStatusIsTrue(submissionId)
            ?: throw UjNotFoundException("Submission not found")
        val contest = submission.contestProblem!!.contest
        if(contest != null) {
            // check if the professor is the owner of the contest
            // TODO: call uj-contests to validate
        }
        // generate keycloak token
        val token = "Bearer ${keycloakBl.getToken()}"

        val submissionDto = SubmissionDto()
        submissionDto.submissionId = submission.submissionId
        // call uj-problems to get problem info
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
        val submission = submissionRepository.findBySubmissionIdAndStatusIsTrue(submissionId)
            ?: throw UjNotFoundException("Submission not found")
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
        return SubmissionStatusDto(
            submissionId = submission.submissionId,
            isDone = finished,
            testcases = submissionStatusList,
            testcaseCount = testcaseCount
        )
    }
}
