package it.polito.wa2.g19.server.profiles.vendors

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/API/vendor")
@Observed
@CrossOrigin
class VendorController(
    private val vendorService: VendorService
) {

    @GetMapping("/profiles")
    @ResponseStatus(HttpStatus.OK)
    fun getProfiles(
        principal: JwtAuthenticationToken
    ): List<VendorDTO> {
        return vendorService.getAll()
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun getProfile(
        @Valid
        @PathVariable
        @Email(message = "provide a valid email")
        email: String
    ): VendorDTO {
        println(email)
        return vendorService.getVendor(email)
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun insertProfile(
        @RequestBody
        credentials: VendorCredentialsDTO
    ) {
        vendorService.insertVendor(credentials)
    }
}