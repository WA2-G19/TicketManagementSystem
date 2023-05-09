package it.polito.wa2.g19.server.profiles.customers

import it.polito.wa2.g19.server.profiles.NotMatchingEmailException
import jakarta.validation.constraints.Email
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/API")
class CustomerController(
    private val profileService: CustomerService
) {

    // Only Manager
    @PreAuthorize("hasRole('Manager')")
    @GetMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<CustomerDTO> {
        return profileService.getAll()
    }

    // Manager (for all)
    // Client and Expert only for its profile
    @PreAuthorize("hasRole('Manager') or #email == principal.username")
    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @Valid
        @PathVariable
        @Email(message = "provide a valid email")
        email: String
    ): CustomerDTO? {
        return profileService.getProfile(email)
    }

    // Manager
    @PreAuthorize("hasRole('Manager')")
    @PostMapping("/profiles")
    @ResponseStatus(HttpStatus.CREATED)
    fun postProfile(
        @Valid
        @RequestBody
        profile: CustomerDTO
    ) {
        return profileService.insertProfile(profile)
    }

    // Client
    @PreAuthorize("#email == principal.username")
    @PutMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun putProfile(
        @Valid
        @PathVariable
        @Email(message = "provide a valid email")
        email: String,
        @Valid
        @RequestBody
        profile: CustomerDTO
    )
    {
        if (email.trim() != profile.email.trim()) {
            throw NotMatchingEmailException()
        }
        profileService.updateProfile(email, profile)
    }
}