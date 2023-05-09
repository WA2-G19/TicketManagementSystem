package it.polito.wa2.g19.server.profiles.staff

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/API/staff")
class StaffController(

    private val staffService: StaffServiceImpl
){

    // manager and Expert (its profile)

    @PreAuthorize("#email == authentication.principal.getName() && (hasRole('Manager') || hasRole('Expert'))")
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
}