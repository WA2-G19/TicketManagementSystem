package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.NotMatchStaffTypeException
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class StaffDTO(
    @field:Email(message = "provide a valid email")
    @field:NotBlank(message = "email cannot be blank")
    var email: String,
    @field:NotBlank(message = "name cannot be blank" )
    val name: String,
    @field:NotBlank(message = "surname cannot be blank")
    val surname: String,
    @field:NotBlank(message = "type cannot be blank")
    @field:Enumerated(EnumType.STRING)
    val type: StaffType,
    val skills: List<String>
)

enum class StaffType {
    Manager,
    Expert
}

fun Staff.toDTO(): StaffDTO {
    if (this is Expert)
        return StaffDTO(email.trim().lowercase(), name, surname, StaffType.Expert, skills.map { it.name })
    else if (this is Manager)
        return StaffDTO(email.trim().lowercase(), name, surname, StaffType.Manager, skills.map { it.name })
    throw NotMatchStaffTypeException()
}