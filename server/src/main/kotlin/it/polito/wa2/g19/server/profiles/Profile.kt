package it.polito.wa2.g19.server.profiles

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "profile")
class Profile() {

    @Id
    @Email

    @NotNull(message = "email cannot be null")
    var email: String = ""
    @NotNull(message = "name cannot be null")
    var name: String = ""
    @NotNull(message = "surname cannot be null")
    var surname: String = ""

    constructor(email: String, name: String, surname: String) : this(){
        this.email = email
        this.name = name
        this.surname = surname
    }





    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Profile

        if (email != other.email) return false
        if (name != other.name) return false
        if (surname != other.surname) return false

        return true
    }
}
