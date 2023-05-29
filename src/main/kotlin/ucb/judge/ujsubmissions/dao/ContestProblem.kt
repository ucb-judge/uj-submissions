package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "contest_problem")
class ContestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_problem_id")
    var contestProblemId: Long = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    var contest: Contest? = null;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problem_id")
    var problem: Problem? = null;

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contestProblem")
    var clarification: List<Clarification>? = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contestProblem")
    var submissions: List<Submission>? = null;
}