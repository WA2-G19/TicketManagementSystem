package it.polito.wa2.g19.server.products

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional("transactionManager")

class ProductServiceImpl(
    private val productRepository: ProductRepository
): ProductService {

    @PreAuthorize("isAuthenticated()")
    override fun getAll(page: Int, size: Int): PageProductsDTO {
        return if(size > 0 && page > -1) {
            val productPage = productRepository.findAll(Pageable.ofSize(size).withPage(page))
            PageProductsDTO(productPage.toList().map{ it.toDTO() }, productPage.totalPages)
        } else
            PageProductsDTO(productRepository.findAll().map { it.toDTO() }, 1)
    }

    @PreAuthorize("isAuthenticated()")
    override fun getProduct(ean: String): ProductDTO {
        val product = productRepository.findByIdOrNull(ean)
        if(product == null) {
            throw ProductNotFoundException()
        } else {
            return product.toDTO()
        }

    }

    @PreAuthorize("hasRole('Manager')")
    override fun insertProduct(product: ProductDTO) {
        if (productRepository.existsById(product.ean)) {
            throw DuplicatedProductException()
        }
        productRepository.save(Product(product.ean, product.name, product.brand))
    }
}