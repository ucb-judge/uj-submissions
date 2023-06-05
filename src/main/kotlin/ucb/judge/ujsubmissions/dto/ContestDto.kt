package ucb.judge.ujsubmissions.dto

import java.sql.Timestamp

data class ContestDto(
    var contestId: Long,
    var name: String,
    var description: String,
    var startDate: Timestamp,
    var endDate: Timestamp,
    var professor: ProfessorDto? = null,
    var subject: SubjectDto? = null,
    var isPublic: Boolean,
)
