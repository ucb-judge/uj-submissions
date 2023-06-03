package ucb.judge.ujsubmissions.amqp.consumer

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import ucb.judge.ujsubmissions.bl.KeycloakBl
import ucb.judge.ujsubmissions.dao.S3Object
import ucb.judge.ujsubmissions.dao.TestcaseSubmission
import ucb.judge.ujsubmissions.dao.repository.SubmissionRepository
import ucb.judge.ujsubmissions.dao.repository.TestcaseRepository
import ucb.judge.ujsubmissions.dao.repository.TestcaseSubmissionRepository
import ucb.judge.ujsubmissions.dao.repository.VerdictTypeRepository
import ucb.judge.ujsubmissions.dto.VerdictDto
import ucb.judge.ujsubmissions.service.KeycloakService
import ucb.judge.ujsubmissions.service.UjFileUploaderService
import ucb.judge.ujsubmissions.utils.FileUtils

@Component
class VerdictConsumer constructor(
    private val submissionRepository: SubmissionRepository,
    private val testcaseRepository: TestcaseRepository,
    private val verdictTypeRepository: VerdictTypeRepository,
    private val testcaseSubmissionRepository: TestcaseSubmissionRepository,
    private val ujFileUploaderService: UjFileUploaderService,
    private val keycloakBl: KeycloakBl
) {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(VerdictConsumer::class.java)
    }

    @RabbitListener(queues = ["verdict2Queue"])
    fun receiveVerdict(verdictDto: VerdictDto) {
        logger.info("Received verdict for submission ${verdictDto.submissionId}")
        // find submission by id
        val submission = submissionRepository.findById(verdictDto.submissionId).get()
        // find testcase by id
        val testcase = testcaseRepository.findById(verdictDto.testcaseId).get()
        // find verdict by id
        val verdict = verdictTypeRepository.findById(verdictDto.verdictId).get()
        logger.info("Verdict for testcase #${testcase.testcaseNumber}: ${verdict.abbreviation}")
        // store output in Minio
        val token = "Bearer ${keycloakBl.getToken()}"
        val s3Output = ujFileUploaderService.uploadFile(
            FileUtils.createSubmissionFile(verdictDto.output, "txt"),
            "submissions-output",
            token
        ).data
        val outputEntity = S3Object()
        outputEntity.s3ObjectId = s3Output!!.s3ObjectId
        outputEntity.contentType = s3Output.contentType
        outputEntity.bucket = s3Output.bucket
        outputEntity.filename = s3Output.filename
        // create testcase_submission
        val testcaseSubmission = TestcaseSubmission()
        testcaseSubmission.submission = submission
        testcaseSubmission.s3Output = outputEntity
        testcaseSubmission.testcase = testcase
        testcaseSubmission.verdictType = verdict
        testcaseSubmission.memory = verdictDto.executionMemory
        testcaseSubmission.time = verdictDto.executionTime
        testcaseSubmissionRepository.save(testcaseSubmission)
        // check if is last testcase
        if (verdictDto.isLast) {
            // update submission with the final verdict
            logger.info("Verdict for submission ${verdictDto.submissionId}: ${verdict.abbreviation}")
            submission.verdictType = verdict
            submissionRepository.save(submission)
        }
    }
}