package it.polito.wa2.g19.server.profiles

import it.polito.wa2.g19.server.common.EntityBase
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class Profile(): EntityBase<Int>() {

    @Column(unique = true, nullable = false, name = "email")
    open var email: String = ""
    @Column(nullable = false)
    open var name: String = ""
    @Column(nullable = false)
    open var surname: String = ""

    constructor(email: String, name: String, surname: String) : this() {
        this.email = email
        this.name = name
        this.surname = surname
    }

}