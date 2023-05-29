package ucb.judge.ujsubmissions.bl

import org.springframework.stereotype.Service
import ucb.judge.ujsubmissions.dao.Language
import ucb.judge.ujsubmissions.dao.Submission
import ucb.judge.ujsubmissions.dao.repository.LanguageRepository
import ucb.judge.ujsubmissions.dao.repository.SubmissionRepository
import ucb.judge.ujsubmissions.dto.FileMetadataDto
import ucb.judge.ujsubmissions.dto.NewSubmissionDto
import ucb.judge.ujsubmissions.service.UjFileUploaderService
import ucb.judge.ujsubmissions.utils.FileUtils
import java.sql.Timestamp
import java.util.*

@Service
class SubmissionBl constructor(
    private val ujFileUploaderService: UjFileUploaderService,
    private val languageRepository: LanguageRepository,
    private val submissionRepository: SubmissionRepository
) {

    fun createSubmission(submission: NewSubmissionDto): Long {
        return 1;
    }
}
