package it.polito.wa2.g19.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginDTO(
    @field:Email(message = "username is a valid email")
    @field:NotBlank(message = "username cannot be blank")
    val username: String,
    @field:NotBlank(message = "password cannot be blank")
    val password: String
)