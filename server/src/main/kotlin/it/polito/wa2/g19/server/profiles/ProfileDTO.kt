package it.polito.wa2.g19.server.profiles

data class ProfileDTO(
    val email: String,
    val name: String,
    val surname: String
)

fun Profile.toDTO(): ProfileDTO = ProfileDTO(email, name, surname)