package it.polito.wa2.g19.server.products

import org.hibernate.validator.constraints.EAN
import jakarta.validation.constraints.NotNull


data class ProductDTO (
    @field:EAN(message = "ean is not valid")
    @field:NotNull(message = "ean cannot be null")
    val ean: String,
    @field:NotNull(message = "name cannot be null")
    val name: String,
    @field:NotNull(message = "brand cannot be null")
    val brand: String
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(ean, name, brand)
}
