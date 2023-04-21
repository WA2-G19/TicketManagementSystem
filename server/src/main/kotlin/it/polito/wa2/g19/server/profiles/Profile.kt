package it.polito.wa2.g19.server.profiles

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

@MappedSuperclass
open class Profile {
    @Id
    @Email
    @NotNull(message = "email cannot be null")
    open var email: String = ""
    @NotNull(message = "name cannot be null")
    open var name: String = ""
    @NotNull(message = "surname cannot be null")
    open var surname: String = ""
}