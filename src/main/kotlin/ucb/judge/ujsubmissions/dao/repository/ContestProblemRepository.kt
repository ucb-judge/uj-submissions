package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubmissions.dao.ContestProblem

interface ContestProblemRepository: JpaRepository<ContestProblem, Long> {
    fun findByContestProblemIdAndStatusIsTrue(id: Long): ContestProblem?
}