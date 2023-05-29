package it.polito.wa2.g19.server.products

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import org.hibernate.validator.constraints.EAN
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/API")
@Observed
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/products")
    fun getAll(): List<ProductDTO> {
        return productService.getAll()
    }

    @GetMapping("/products/{ean}")
    fun getProduct(
        @Valid
        @PathVariable
        @EAN(message = "ean is not valid")
        ean: String
    ): ProductDTO {

        return productService.getProduct(ean)
    }

}