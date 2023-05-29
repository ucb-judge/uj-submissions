package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "verdict_type")
class VerdictType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verdict_type_id")
    var verdictTypeId: Long = 0;

    @Column(name = "description")
    var description: String = "";

    @Column(name = "abbreviation")
    var abbreviation: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "verdictType")
    var testcaseSubmissions: List<TestcaseSubmission>? = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "verdictType")
    var submissions: List<Submission>? = null;
}