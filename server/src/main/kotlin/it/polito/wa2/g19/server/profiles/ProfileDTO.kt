package it.polito.wa2.g19.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class ProfileDTO(
    @field:Email
    @field:NotBlank(message = "email cannot be blank")
    val email: String,
    @field:NotBlank(message = "name cannot be blank")
    val name: String,
    @field:NotNull(message = "surname cannot be null")
    val surname: String
)

fun Profile.toDTO(): ProfileDTO = ProfileDTO(email, name, surname)