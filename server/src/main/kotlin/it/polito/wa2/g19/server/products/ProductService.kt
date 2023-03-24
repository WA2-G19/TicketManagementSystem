package it.polito.wa2.g19.server.products

interface ProductService {

    fun getAll(): List<ProductDTO>
    fun getProduct(ean: String): ProductDTO?

}