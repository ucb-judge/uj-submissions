package ucb.judge.ujsubmissions.dto

import ucb.judge.ujsubmissions.dao.TestcaseSubmission
import java.sql.Timestamp

data class SubmissionDto(
    var submissionId: Long = 0,
    var problem: ProblemMinimalDto = ProblemMinimalDto(),
    var language: LanguageDto = LanguageDto(),
    var sourceCode: String = "",
    var submissionDate: Timestamp = Timestamp(0),
    var verdict: VerdictTypeDto = VerdictTypeDto(),
    var testcases: List<TestcaseSubmissionDto> = listOf(),
)
