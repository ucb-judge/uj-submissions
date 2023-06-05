package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubmissions.dao.ContestProblem
import ucb.judge.ujsubmissions.dao.Student
import ucb.judge.ujsubmissions.dao.Submission
import ucb.judge.ujsubmissions.dao.VerdictType

interface SubmissionRepository : JpaRepository<Submission, Long> {

    fun findBySubmissionIdAndStatusIsTrue(submissionId: Long): Submission?
    fun countByContestProblemAndVerdictTypeAndStudent(contestProblem: ContestProblem, verdictType: VerdictType, student: Student): Long
}