package ucb.judge.ujsubmissions.dao.repository

import org.springframework.data.jpa.repository.JpaRepository
import ucb.judge.ujsubmissions.dao.Language

interface LanguageRepository : JpaRepository<Language, Long> {
}