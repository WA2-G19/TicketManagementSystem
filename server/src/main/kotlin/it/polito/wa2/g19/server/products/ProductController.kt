package it.polito.wa2.g19.server.products

import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.hibernate.validator.constraints.EAN
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/API")
@Observed
@Slf4j
class ProductController(
    private val productService: ProductService
) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)
    @GetMapping("/products")
    fun getAll(): List<ProductDTO> {
        log.info("Getting all products")
        return productService.getAll()
    }

    @GetMapping("/products/{ean}")
    fun getProduct(
        @Valid
        @PathVariable
        @EAN(message = "ean is not valid")
        ean: String
    ): ProductDTO {
        log.info("Getting product with EAN {}", ean)
        return productService.getProduct(ean)
    }

}