package it.polito.wa2.g19.server.products

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.EAN


data class ProductDTO (
    @field:EAN(message = "ean is not valid")
    @field:NotBlank(message = "ean cannot be blank")
    val ean: String,
    @field:NotBlank(message = "name cannot be blank")
    val name: String,
    @field:NotBlank(message = "brand cannot be blank")
    val brand: String
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(ean, name, brand)
}


data class PageProductsDTO(
    val products: List<ProductDTO>,
    val totalPages: Int
)