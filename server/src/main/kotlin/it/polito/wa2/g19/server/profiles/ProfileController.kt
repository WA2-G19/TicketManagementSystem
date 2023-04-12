package it.polito.wa2.g19.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API")
class ProfileController(
    private val profileService: ProfileService
) {
    /*@GetMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<ProfileDTO> {
        return profileService.getAll()
    }*/

    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @Valid
        @PathVariable
        @Email
        email: String
    ): ProfileDTO? {
        return profileService.getProfile(email)
    }

    @PostMapping("/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun postProfile(
        @Valid
        @RequestBody
        profile: ProfileDTO
    ) {
        return profileService.insertProfile(profile)
    }

    @PutMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun putProfile(
        @Valid
        @PathVariable
        @Email
        email: String,
        @Valid
        @RequestBody
        profile: ProfileDTO
    ) {
        if (email.trim().lowercase() != profile.email.trim().lowercase()) {
            throw NotMatchingEmailException()
        }
        profileService.updateProfile(email, profile)
    }
}