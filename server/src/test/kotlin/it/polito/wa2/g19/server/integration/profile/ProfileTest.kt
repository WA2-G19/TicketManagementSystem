package it.polito.wa2.g19.server.integration.profile

import it.polito.wa2.g19.server.profiles.customers.CustomerDTO
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class ProfileTest {
    companion object {

        @Container
        val postgres = PostgreSQLContainer("postgres:latest")
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
        }
    }
    @LocalServerPort
    protected var port: Int = 0
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var customerRepository: CustomerRepository







    @Test
    fun containerIsRunning() {
        assertThat(postgres.isRunning).isTrue
    }
    @Test
    fun postProfile(){
        val profile = CustomerDTO("jacopo@test.it", "jacopo", "De Cristofaro", "Candida")
        val request = HttpEntity(profile)
        val response = restTemplate.postForEntity<Unit>("/API/profiles", request, HttpMethod.POST)
        println(response.statusCode)
        assertThat(response.statusCode)
    }
     @Test
     fun getProfile(){
         val response = restTemplate.getForEntity<CustomerDTO>("/API/profiles/jacopo@test.it")
         assertThat(response.statusCode.is2xxSuccessful)
     }


}
