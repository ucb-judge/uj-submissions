package ucb.judge.ujsubmissions.dto

data class SubmissionStatusDto(
    var submissionId: Long = 0,
    var testcases: List<TestcaseStatusDto> = listOf(),
    var isDone: Boolean = false,
    var testcaseCount : Int = 0,
)
