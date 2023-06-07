package it.polito.wa2.g19.server.profiles.vendors

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class VendorDTO(
    @NotBlank
    @Email
    val email: String,
    @NotBlank
    val businessName: String,
    @NotBlank
    @Pattern(regexp = "(\\+[0-9]{2})?[0-9]{10}")
    val phoneNumber: String,
    @NotBlank
    val address: String
)

fun Vendor.toDTO() = VendorDTO(email, businessName, phoneNumber, address)

data class VendorCredentialsDTO(
    @NotNull
    val vendor: VendorDTO,
    @NotBlank
    val password: String
)