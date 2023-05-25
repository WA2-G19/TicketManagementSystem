package it.polito.wa2.g19.server.profiles

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.UUID

@MappedSuperclass
open class Profile() {

    @Id
    @Column(nullable = false)
    open var id: UUID? = null
    @Column(unique = true, nullable = false)
    open var email: String = ""
    @Column(nullable = false)
    open var name: String = ""
    @Column(nullable = false)
    open var surname: String = ""

    constructor(email: String, name: String, surname: String) : this() {
        this.apply {
            this.email = email
            this.name = name
            this.surname = surname
        }
    }

}