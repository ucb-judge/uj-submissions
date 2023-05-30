package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubmissions.dao.Testcase

@Repository
interface TestcaseRepository: JpaRepository<Testcase, Long> {
}