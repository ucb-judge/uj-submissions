package ucb.judge.ujsubmissions.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "problem")
class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    var problemId: Long = 0;

    @Column(name = "title")
    var title: String = "";

    @OneToOne
    @JoinColumn(name = "s3_description_id")
    var s3Description: S3Object? = null;

    @Column(name = "max_time")
    var maxTime: Double = 0.0;

    @Column(name = "max_memory")
    var maxMemory: Int = 0;

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "problem")
    var contestProblems: List<ContestProblem>? = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "problem")
    var testcases: List<Testcase>? = null;
}