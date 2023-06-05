package ucb.judge.ujsubmissions.dto

import java.util.*

data class SubjectDto(
    var subjectId: Long,
    var name: String,
    var code: String,
    var dateFrom: Date,
    var dateTo: Date,
)
