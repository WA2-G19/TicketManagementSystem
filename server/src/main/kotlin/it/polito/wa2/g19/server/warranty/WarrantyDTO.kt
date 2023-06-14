package it.polito.wa2.g19.server.warranty

import jakarta.validation.constraints.AssertFalse
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.EAN
import org.hibernate.validator.constraints.UUID
import org.springframework.boot.convert.DurationFormat
import org.springframework.boot.convert.DurationStyle
import java.time.LocalDateTime
import java.time.Period

data class WarrantyInDTO(
    @NotBlank
    @EAN
    val productEan: String,

    @NotBlank
    @DurationFormat(DurationStyle.ISO8601)
    val duration: Period
)

data class WarrantyOutDTO(
    @NotBlank
    @UUID
    val id: String,
    @NotBlank
    @EAN
    val productEan: String,
    @NotBlank
    @Email
    val vendorEmail: String,
    @Email
    val customerEmail: String?,
    val creationTimestamp: LocalDateTime,
    val activationTimestamp: LocalDateTime?,
    @DurationFormat(DurationStyle.ISO8601)
    val duration: Period
) {
    @AssertFalse
    fun isValid(): Boolean = (customerEmail != null) and (activationTimestamp != null)

}

fun Warranty.toDTO() = WarrantyOutDTO(id.toString(), product.ean, vendor.email, customer?.email, creationTimestamp, activationTimestamp, duration)