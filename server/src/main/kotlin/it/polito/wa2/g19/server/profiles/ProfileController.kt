package it.polito.wa2.g19.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileService: ProfileService
) {
    @GetMapping("/API/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<ProfileDTO> {
        return profileService.getAll()
    }

    @GetMapping("/API/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @Valid
        @PathVariable
        @Email
        email: String
    ): ProfileDTO? {
        return profileService.getProfile(email)
    }

    @PostMapping("/API/profiles")
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
        @RequestBody
        profile: ProfileDTO,
        @Valid
        @PathVariable
        @Email
        email: String
    ) {
        profileService.updateProfile(email, profile)
    }
}