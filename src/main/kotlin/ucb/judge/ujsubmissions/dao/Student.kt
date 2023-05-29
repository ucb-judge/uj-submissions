package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "student")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    var studentId: Long = 0;

    @Column(name = "kc_uuid")
    var kcUuid: String = "";

    @Column(name = "first_name")
    var firstName: String = "";

    @Column(name = "last_name")
    var lastName: String = "";

    @Column(name = "email")
    var email: String = "";

    @Column(name = "password")
    var password: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    var submissions: List<Submission>? = null;
}