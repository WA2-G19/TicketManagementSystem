package it.polito.wa2.g19.server.skills

interface SkillService {

    fun getAll(): List<SkillDTO>

    fun insertSkill(skill: SkillDTO)

    fun deleteSkill(skill: SkillDTO)
}