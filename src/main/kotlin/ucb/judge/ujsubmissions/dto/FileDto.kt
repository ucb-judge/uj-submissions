package ucb.judge.ujsubmissions.dto

data class FileDto(
    val fileId : Long = 0,
    val bucket: String = "",
    val filename: String = "",
    val contentType: String = ""
)
