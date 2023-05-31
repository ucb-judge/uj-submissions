package ucb.judge.ujsubmissions.dto

data class TestcaseStatusDto(
    var testcaseId: Long = 0,
    var verdictType: VerdictTypeDto,
    var memory: Long = 0,
    var time: Double = 0.0,
)
