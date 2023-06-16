package it.polito.wa2.g19.server.skills

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/API/skill")
@Observed
@CrossOrigin
class SkillController(
    private val skillService: SkillService
) {

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<SkillDTO> {
        return skillService.getAll()
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun insertSkill(@Valid @RequestBody skill: SkillDTO): SkillDTO {
        skillService.insertSkill(skill)
        return skill
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun deleteSkill(@Valid @RequestBody skill: SkillDTO): SkillDTO {
        skillService.deleteSkill(skill)
        return skill
    }
}