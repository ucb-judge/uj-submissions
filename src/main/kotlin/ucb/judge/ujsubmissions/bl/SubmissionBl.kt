package ucb.judge.ujsubmissions.bl

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ucb.judge.ujsubmissions.amqp.producer.SubmissionProducer
import ucb.judge.ujsubmissions.dao.*
import ucb.judge.ujsubmissions.dao.repository.LanguageRepository
import ucb.judge.ujsubmissions.dao.repository.SubmissionRepository
import ucb.judge.ujsubmissions.dto.NewSubmissionDto
import ucb.judge.ujsubmissions.dto.SubmissionInfoDto
import ucb.judge.ujsubmissions.exception.UjNotFoundException
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
    private val keycloakBl: KeycloakBl,
    private val languageRepository: LanguageRepository,
    private val submissionRepository: SubmissionRepository,
    private val submissionProducer: SubmissionProducer
) {

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
}
