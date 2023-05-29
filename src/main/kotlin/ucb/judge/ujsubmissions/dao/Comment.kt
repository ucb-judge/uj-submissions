package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "comment")
class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var commentId: Long = 0;

    @Column(name = "kc_uuid")
    var kcUuid: String = "";

    @Column(name = "comment")
    var comment: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    var submission: Submission? = null;
}