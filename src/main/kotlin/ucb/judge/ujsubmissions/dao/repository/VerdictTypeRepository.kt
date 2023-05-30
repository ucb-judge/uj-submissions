package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ucb.judge.ujsubmissions.dao.VerdictType

@Repository
interface VerdictTypeRepository: JpaRepository<VerdictType, Long> {
}