package ucb.judge.ujsubmissions.dao

import javax.persistence.*

@Entity
@Table(name = "language")
class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id")
    var languageId: Long = 0;

    @Column(name = "name")
    var name: String = "";

    @Column(name = "extension")
    var extension: String = "";

    @Column(name = "status")
    var status: Boolean = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "language")
    var submissions: List<Submission>? = null;

    constructor(name: String, extension: String, status: Boolean) {
        this.name = name
        this.extension = extension
        this.status = status
    }
}