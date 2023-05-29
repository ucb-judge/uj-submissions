package ucb.judge.ujsubmissions.dto

data class NewSubmissionDto(
    val problemId: Long = 0,
    val contestId: Long? = null,
    val sourceCode: String = "",
    val language: Long = 0
)
