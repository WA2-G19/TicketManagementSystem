package it.polito.wa2.g19.server.profiles.customers

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g19.server.profiles.NotMatchingEmailException
import jakarta.validation.constraints.Email
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@CrossOrigin
@RequestMapping("/API")
@Observed
class CustomerController(
    private val profileService: CustomerService
) {

    @GetMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<CustomerDTO> {
        return profileService.getAll()
    }

    @GetMapping("/profiles/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @Valid
        @PathVariable
        @Email(message = "provide a valid email")
        email: String
    ): CustomerDTO? {
        println(email)
        return profileService.getProfile(email)
    }

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

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @RequestBody(required = true)
        customer: CredentialCustomerDTO
    ) {
        profileService.signup(customer)
    }
}