package it.polito.wa2.g19.server.profiles.staff

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/API/staff")
@Observed
@CrossOrigin
class StaffController(

    private val staffService: StaffService
){

    @GetMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getProfiles(
        principal: JwtAuthenticationToken
    ): List<StaffDTO>{
        return staffService.getAll()
    }

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


    @PostMapping("/createExpert")
    @ResponseStatus(HttpStatus.CREATED)
    fun createExpert(
        @RequestBody
        credentials: CredentialStaffDTO
    ) {
        staffService.createExpert(credentials)
    }
}