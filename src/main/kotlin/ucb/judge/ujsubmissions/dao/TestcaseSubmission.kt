package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "testcase_submission")
class TestcaseSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_submission_id")
    var testcaseSubmissionId: Long = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "submission_id")
    var submission: Submission? = null;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "testcase_id")
    var testcase: Testcase? = null;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verdict_type_id")
    var verdictType: VerdictType? = null;

    @OneToOne
    @JoinColumn(name = "s3_output")
    var s3Output: S3Object? = null;

    @Column(name = "memory")
    var memory: Long = 0;

    @Column(name = "time")
    var time: Double = 0.0;

    @Column(name = "status")
    var status: Boolean = true;
}