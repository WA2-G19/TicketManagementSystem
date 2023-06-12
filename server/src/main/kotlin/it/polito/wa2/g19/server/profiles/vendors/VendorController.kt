package it.polito.wa2.g19.server.profiles.vendors

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@CrossOrigin
@RequestMapping("/API/vendor")
@Observed
class VendorController(
    private val vendorService: VendorService
){

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun getProfiles(): List<VendorDTO> {
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