package it.polito.wa2.g19.server.products

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional("transactionManager")

class ProductServiceImpl(
    private val productRepository: ProductRepository
): ProductService {

    override fun getAll(page: Int, size: Int): PageProductsDTO {
        return if(size > 0 && page > -1) {
            val page = productRepository.findAll(Pageable.ofSize(size).withPage(page))
            PageProductsDTO(page.toList().map{it.toDTO()}, page.totalPages)
        } else
            PageProductsDTO(productRepository.findAll().map { it.toDTO() }, 1)
    }

    override fun getProduct(ean: String): ProductDTO {
        val product = productRepository.findByIdOrNull(ean)
        if(product == null) {
            throw ProductNotFoundException()
        } else {
            return product.toDTO()
        }

    }

    override fun insertProduct(product: ProductDTO) {
        if (productRepository.existsById(product.ean)) {
            throw DuplicatedProductException()
        }
        productRepository.save(Product(product.ean, product.name, product.brand))
    }
}