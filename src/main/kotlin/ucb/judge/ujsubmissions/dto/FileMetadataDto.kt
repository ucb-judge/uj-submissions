package ucb.judge.ujsubmissions.dto

import org.apache.http.entity.ContentType

data class FileMetadataDto (
    var bucket: String = "",
    var filename: String = "",
    var contentType: String = "",
    var language: String = "",
    var contestProblemId: Long = 0,
)