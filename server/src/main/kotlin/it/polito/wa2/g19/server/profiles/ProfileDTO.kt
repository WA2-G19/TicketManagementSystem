package it.polito.wa2.g19.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class ProfileDTO(
    @field:Email
    @field:NotNull(message = "email cannot be null")
    val email: String,
    @field:NotNull(message = "name cannot be null")
    val name: String,
    @field:NotNull(message = "surname cannot be null")
    val surname: String
)

fun Profile.toDTO(): ProfileDTO = ProfileDTO(email, name, surname)