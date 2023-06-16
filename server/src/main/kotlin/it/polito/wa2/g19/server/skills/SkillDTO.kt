package it.polito.wa2.g19.server.skills

import jakarta.validation.constraints.NotBlank

data class SkillDTO(
    @field:NotBlank
    val name: String
)

fun Skill.toDTO() = SkillDTO(name)