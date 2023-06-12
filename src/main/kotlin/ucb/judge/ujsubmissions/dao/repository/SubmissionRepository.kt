package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubmissions.dao.ContestProblem
import ucb.judge.ujsubmissions.dao.Student
import org.springframework.data.jpa.repository.Query
import ucb.judge.ujsubmissions.dao.Submission
import ucb.judge.ujsubmissions.dao.VerdictType

interface SubmissionRepository : JpaRepository<Submission, Long> {

    fun findBySubmissionIdAndStatusIsTrue(submissionId: Long): Submission?
    fun countByContestProblemAndVerdictTypeAndStudent(contestProblem: ContestProblem, verdictType: VerdictType, student: Student): Long

    @Query("""
        SELECT s FROM Submission s
        WHERE s.status = true
        AND s.student.studentId = :studentId
        ORDER BY s.submissionId DESC
    """)
    fun findAll(pageRequest: PageRequest, studentId:Long): Page<Submission>
}