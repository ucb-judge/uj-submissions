package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubmissions.dao.ContestScoreboard
import ucb.judge.ujsubmissions.dao.Student

@Repository
interface ContestScoreboardRepository: JpaRepository<ContestScoreboard, Long> {
    fun findByContestContestIdAndStudent(contestId: Long, student: Student): ContestScoreboard?
}