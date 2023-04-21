package it.polito.wa2.g19.server.profiles

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "profile")
class Customer(): Profile() {

    @NotNull(message = "address cannot be null")
    var address: String = ""

    constructor(email: String, name: String, surname: String, address: String) : this() {
        this.email = email
        this.name = name
        this.surname = surname
        this.address = address
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Customer

        if (email != other.email) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}
