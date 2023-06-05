package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "contest_scoreboard")
class ContestScoreboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_scoreboard_id")
    var contestScoreboardId: Long = 0;

    @Column(name = "rank")
    var rank: Int = 0;

    @Column(name = "problems_solved")
    var problemsSolved: Int = 0;

    @Column(name = "status")
    var status: Boolean = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    var contest: Contest? = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    var student: Student? = null;
}