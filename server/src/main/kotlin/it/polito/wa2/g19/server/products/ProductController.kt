package it.polito.wa2.g19.server.products

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.hibernate.validator.constraints.EAN
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.Objects

@RestController
@CrossOrigin
@Validated
@RequestMapping("/API")
@Observed
@Slf4j
class ProductController(
    private val productService: ProductService
) {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)
    @GetMapping("/products")
    fun getAll(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int? ,
    ): PageProductsDTO {
        log.info("Getting all products")
        return productService.getAll(page?: 0, size ?: -1)
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

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    fun postProduct(
        @Valid
        @RequestBody
        product: ProductDTO
    ) {
        log.info("Inserting product with EAN {}", product.ean)
        productService.insertProduct(product)
    }
}