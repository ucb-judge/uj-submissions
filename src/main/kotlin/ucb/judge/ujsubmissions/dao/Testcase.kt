package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "testcase")
class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_id")
    var testcaseId: Long = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: Problem? = null;

    @Column(name = "testcase_number")
    var testcaseNumber: Int = 0;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "s3_input")
    var s3Input: S3Object? = null;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "s3_output")
    var s3Output: S3Object? = null;

    @Column(name = "is_sample")
    var isSample: Boolean = false;

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testcase")
    var submissions: List<TestcaseSubmission>? = null;
}