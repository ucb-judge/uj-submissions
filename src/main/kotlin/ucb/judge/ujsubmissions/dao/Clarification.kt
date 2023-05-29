package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "clarification")
class Clarification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var clarificationId: Long = 0;

    @Column(name = "question")
    var question: String = "";

    @Column(name = "response")
    var response: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_problem_id")
    var contestProblem : ContestProblem? = null;
}