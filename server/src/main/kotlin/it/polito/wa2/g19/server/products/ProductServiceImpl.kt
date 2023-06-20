package it.polito.wa2.g19.server.products

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional("transactionManager")

class ProductServiceImpl(
    private val productRepository: ProductRepository
): ProductService {

    override fun getAll(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProduct(ean: String): ProductDTO {
        val product = productRepository.findByIdOrNull(ean)
        if(product == null) {
            throw ProductNotFoundException()
        } else {
            return product.toDTO()
        }

    }

}