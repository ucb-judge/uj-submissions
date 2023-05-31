package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubmissions.dao.Submission

interface SubmissionRepository : JpaRepository<Submission, Long> {

    fun findBySubmissionIdAndStatusIsTrue(submissionId: Long): Submission?
}