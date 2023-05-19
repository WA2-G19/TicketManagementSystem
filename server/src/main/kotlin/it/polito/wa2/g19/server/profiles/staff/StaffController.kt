package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.customers.CredentialCustomerDTO
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/API/staff")
class StaffController(

    private val staffService: StaffServiceImpl
){

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @Valid
        @PathVariable
        @Email(message = "provide a valid email")
        email: String
    ): StaffDTO {
        return staffService.getStaff(email)
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    fun signupExpert(
        @RequestBody
        credentials: CredentialCustomerDTO
    ) {
        staffService.signupExpert(credentials)
    }
}