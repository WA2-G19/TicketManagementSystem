package it.polito.wa2.g19.server.profiles

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
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
    @GetMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<ProfileDTO> {
        return profileService.getAll()
    }

    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @PathVariable
        @Email
        @NotNull
        email: String
    ): ProfileDTO? {
        return profileService.getProfile(email)
    }

    @PostMapping("/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun postProfile(
        @RequestBody
        profile: ProfileDTO
    ) {
        profileService.insertProfile(profile)
    }

    @PutMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun putProfile(
        @RequestBody
        profile: ProfileDTO
    ) {
        profileService.updateProfile(profile)
    }
}