package it.polito.wa2.g19.server.skills

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class SkillServiceImpl(
    private val skillRepository: SkillRepository
): SkillService {

    @PreAuthorize("hasRole('Manager')")
    override fun getAll(): List<SkillDTO> {
        return skillRepository.findAll().map {
            it.toDTO()
        }
    }

    @PreAuthorize("hasRole('Manager')")
    override fun insertSkill(skill: SkillDTO) {
        val s = Skill().apply {
            name = skill.name
        }
        skillRepository.save(s)
    }

    @PreAuthorize("hasRole('Manager')")
    override fun deleteSkill(skill: SkillDTO) {
        skillRepository.deleteById(skill.name)
    }
}