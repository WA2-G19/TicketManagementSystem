package it.polito.wa2.g19.server.products

interface ProductService {

    fun getAll(page: Int, size: Int): PageProductsDTO
    fun getProduct(ean: String): ProductDTO
    fun insertProduct(product: ProductDTO)
}