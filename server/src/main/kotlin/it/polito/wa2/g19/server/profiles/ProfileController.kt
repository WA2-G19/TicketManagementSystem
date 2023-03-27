package it.polito.wa2.g19.server.profiles

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(
    private val profileService: ProfileService
) {
    @GetMapping("/profiles")
    fun getAll(): List<ProfileDTO> {
        return profileService.getAll()
    }

    @GetMapping("/profiles/{email}")
    fun getProfile(
        @PathVariable
        email: String
    ): ProfileDTO? {
        return profileService.getProfile(email)
    }

    @PostMapping("/profiles")
    fun postProfile(
        @RequestBody
        profile: ProfileDTO
    ) {
        profileService.insertProfile(profile)
    }

    @PutMapping("/profiles/{email}")
    fun putProfile(
        @RequestBody
        profile: ProfileDTO
    ) {
        profileService.updateProfile(profile)
    }
}