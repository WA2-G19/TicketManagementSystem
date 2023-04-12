package it.polito.wa2.g19.server.products

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.*

internal class ProductsServiceImplTest {
    @Test
    fun `loading an existing product should return a valid DTO specifying its ean`() {
        //Arrange
        val repo = mockk<ProductRepository>();
        every { repo.findByIdOrNull("eanTest") } answers  {
            Product("eanTest","nameTest","brandTest")
        }
        val service = ProductServiceImpl(repo);
        //Act
        val dto = service.getProduct("eanTest")
        //Assert
        assert(dto?.ean.equals("eanTest"))
        assert(dto?.name.equals("nameTest"))
        assert(dto?.brand.equals("brandTest"))
    }
    
    @Test
    fun `loading two products should returns the correct DTO specifying its ean`(){
        val repo = mockk<ProductRepository>();
        every { repo.findByIdOrNull("eanTest") } answers  {
            Product("eanTest","nameTest","brandTest")
        }
        every { repo.findByIdOrNull("eanTestWrong") } answers  {
            Product("eanTestWrong","nameTestWrong","brandTestWrong")
        }
        
        val service = ProductServiceImpl(repo);
        //Act
        val dto = service.getProduct("eanTest")
        //Assert
        assert(dto?.ean.equals("eanTest"))
        assert(dto?.name.equals("nameTest"))
        assert(dto?.brand.equals("brandTest"))
    }
    
    @Test
    fun `loading no products should throws a ProductNotFoundException instance whether the ean specified`() {
        //Arrange
        val repo = mockk<ProductRepository>();

        every { repo.findByIdOrNull("notExistingEan") } answers  {
            null
        }
        val service = ProductServiceImpl(repo);
        //Act & Assert
        assertThrows<ProductNotFoundException> { service.getProduct("notExistingEan")}
    }

    @Test
    fun `loading a product should throws a ProductNotFoundException instance specifying a not existing ean`() {
        //Arrange
        val repo = mockk<ProductRepository>();


        every { repo.findByIdOrNull("existingEan") } answers  {
            Product("existingEan", "existingName", "existingBrand")
        }
        every { repo.findByIdOrNull("notExistingEan") } answers  {
            null
        }
        val service = ProductServiceImpl(repo);
        //Act & Assert
        assertThrows<ProductNotFoundException> { service.getProduct("notExistingEan")}
    }

    @Test
    fun `loading several products should returns all of them`() {
        //Arrange
        val repo = mockk<ProductRepository>();
        every { repo.findAll() } answers  {
            (0..9).map { index -> Product("${index}Ean", "${index}Name","${index}Brand") }
        }
        val service = ProductServiceImpl(repo);
        //Act & Assert
        var productsDTO = service.getAll()

        productsDTO.sortedBy { p -> p.ean }.forEachIndexed{index, p ->
            println(p)
            assert("${index}Ean" == p.ean)
            println("okk")
            assert("${index}Name" == p.name)
            assert("${index}Brand" == p.brand)
        }
    }

    @Test
    fun `loading no products should returns empty collection`() {
        //Arrange
        val repo = mockk<ProductRepository>();
        every { repo.findAll() } answers  {
            listOf()
        }
        val service = ProductServiceImpl(repo);
        //Act & Assert
        var productsDTO = service.getAll()
        assert(productsDTO.isEmpty())
    }
    


}