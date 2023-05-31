package ucb.judge.ujsubmissions.dto

data class TestcaseSubmissionDto (
    var testcaseId: Long = 0,
    var input: String = "",
    var output: String = "",
    var verdict: VerdictTypeDto = VerdictTypeDto(),
    var time: Double = 0.0,
    var memory: Long = 0,
)