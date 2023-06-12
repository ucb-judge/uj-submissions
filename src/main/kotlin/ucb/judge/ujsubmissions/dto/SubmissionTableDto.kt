package ucb.judge.ujsubmissions.dto
import java.sql.Timestamp

data class SubmissionTableDto (
    var submissionId: Long = 0,
    var problem: ProblemMinimalDto = ProblemMinimalDto(),
    var language: LanguageDto = LanguageDto(),
    var submissionDate: Timestamp = Timestamp(0),
    var verdict: VerdictTypeDto = VerdictTypeDto()
)
