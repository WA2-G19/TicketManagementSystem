package it.polito.wa2.g19.server.integration.profile

import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.wa2.g19.server.Util

import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.LoginDTO
import it.polito.wa2.g19.server.profiles.customers.CredentialCustomerDTO
import it.polito.wa2.g19.server.profiles.customers.CustomerDTO
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.staff.*
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.skills.Skill
import it.polito.wa2.g19.server.skills.SkillRepository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.admin.client.Keycloak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class ProfileTest {

    @Autowired
    private lateinit var keycloak: Keycloak

    @Value("\${keycloak.admin.realm}")
    private lateinit var realmName: String
    companion object {


        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @Container
        val keycloak: KeycloakContainer = KeycloakContainer("quay.io/keycloak/keycloak:latest")
            .withRealmImportFile("keycloak/realm.json")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            val keycloakBaseUrl = keycloak.authServerUrl
            registry.add("keycloakBaseUrl") { keycloakBaseUrl }
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { "${keycloakBaseUrl}/realms/ticket_management_system" }
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri") {"${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs"}
            registry.add("keycloak.admin.realm"){"ticket_management_system"}
            registry.add("keycloak.admin.username"){"admin@test.it"}
            registry.add("keycloak.admin.password"){"password"}
            registry.add("keycloak.auth-server-url"){keycloakBaseUrl}

        }
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    private var mainCustomerEmail = "newCustomer@test.it".lowercase()
    private var mainExpertEmail = "newExpert@test.it".lowercase()


    private lateinit var manager: Manager

    private lateinit var managerToken: String

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var skillRepository: SkillRepository

    @Autowired
    lateinit var staffRepository: StaffRepository



    @BeforeEach
    fun populateDatabase() {
        if(!postgres.isRunning) postgres.start()
        println("----populating database------")
        manager = Util.mockMainManager()
        manager.id = UUID.randomUUID()
        staffRepository.save(manager)

        skillRepository.save(Skill().apply {
            name = "Hardware"
        })

        println("---------------------------------")
    }

    @AfterEach
    fun destroyDatabase() {
        println("----destroying database------")
        for(u in listOf(mainCustomerEmail, mainExpertEmail)){
            keycloak.realm(realmName).users().search(u).let {
                if (it.isEmpty()) return@let null
                val user = it.first()
                keycloak.realm(realmName).users().delete(user.id)
            }
        }
        if (!postgres.isRunning){
            return
        }

        customerRepository.deleteAll()
        staffRepository.deleteAll()
        skillRepository.deleteAll()
        println("---------------------------------")
    }

    @BeforeEach
    fun refreshManagerToken(){
        val loginDTO = LoginDTO(manager.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        managerToken = response.body!!
    }



    fun mockCustomerDTO(): CustomerDTO{
        return CustomerDTO(mainCustomerEmail, "newCustomerName",
            "newCustomerSurname", "newCustomerAddress")
    }

    fun mockCredentialCustomerDTO(): CredentialCustomerDTO{
        val customerDTO = mockCustomerDTO()
        val pwd = "secretpwd"
        return CredentialCustomerDTO(customerDTO, pwd)
    }

    fun mockExpertDTO(): StaffDTO{
        return StaffDTO(mainExpertEmail, "newExpertName",
            "newExpertSurname", StaffType.Expert, listOf("Hardware"))

    }

    fun mockCredentialStaffDTO(): CredentialStaffDTO{
        val pwd = "secretpwd"
        return CredentialStaffDTO(mockExpertDTO(), pwd)
    }



    @Test
    fun `signup a new customer with unique email is successful`(){
        val newCustomer = mockCredentialCustomerDTO()
        val request = HttpEntity(newCustomer)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)
        val customer = customerRepository.findByEmailIgnoreCase(newCustomer.customerDTO.email)!!
        assert(customer.email == newCustomer.customerDTO.email)
        assert(customer.name == newCustomer.customerDTO.name)
        assert(customer.surname == newCustomer.customerDTO.surname)
        assert(customer.address == newCustomer.customerDTO.address)
        val ku = keycloak.realm(realmName).users().searchByUsername(mainCustomerEmail, true).first()
        assert(ku.email == newCustomer.customerDTO.email)
        assert(ku.lastName == newCustomer.customerDTO.surname)
        assert(ku.attributes["address"]!!.first() == newCustomer.customerDTO.address)
        assert(ku.username == newCustomer.customerDTO.email)
        assert(ku.firstName == newCustomer.customerDTO.name)
        assert(keycloak.realm(realmName).users().searchByUsername(mainCustomerEmail, true).isNotEmpty() )


    }

    @Test
    fun `signup and login is successful`(){
        val newCustomer = mockCredentialCustomerDTO()
        val request = HttpEntity(newCustomer)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)
        keycloak.realm(realmName).users().list().forEach {
            it.isEmailVerified = true
        }
        val loginResponse: ResponseEntity<String> = restTemplate.exchange("/API/login", HttpMethod.POST, HttpEntity(LoginDTO(newCustomer.customerDTO.email, newCustomer.password)))
        assert(loginResponse.statusCode == HttpStatus.OK)
    }

    @Test
    fun `signup two times is unsuccessful`(){
        val newCustomer = mockCredentialCustomerDTO()
        val request = HttpEntity(newCustomer)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)

        val responseTwo = restTemplate.exchange<DuplicateEmailException>("/API/signup",  HttpMethod.POST, request)
        assert(responseTwo.statusCode == HttpStatus.CONFLICT)
        assertThat(responseTwo.body!!.message == DuplicateEmailException().message)
    }

    @Test
    fun `signup without password is unsuccessful`(){
        val newCustomer = mockCredentialCustomerDTO()
        val customerJson = hashMapOf<String, Any>(
            "customerDTO" to newCustomer.customerDTO
        )
        val request = HttpEntity(customerJson)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(customerRepository.findByEmailIgnoreCase(newCustomer.customerDTO.email) == null)
        assert(keycloak.realm(realmName).users().searchByUsername(mainCustomerEmail, true).isEmpty() )
    }





    @Test
    fun `create a expert is successful`(){
        val newExpert = mockCredentialStaffDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(newExpert, headers)
        val response = restTemplate.postForEntity<Unit>("/API/staff/createExpert", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)


    }

    @Test
    fun `create a expert and login as him is successful`(){
        val newExpert = mockCredentialStaffDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(newExpert, headers)
        val response: ResponseEntity<Unit> = restTemplate.exchange("/API/staff/createExpert", HttpMethod.POST, request)
        assert(response.statusCode == HttpStatus.CREATED)
        keycloak.realm("ticket_")
        val loginResponse: ResponseEntity<String> = restTemplate.exchange("/API/login", HttpMethod.POST, HttpEntity(LoginDTO(newExpert.staffDTO.email, newExpert.password)))
        assert(loginResponse.statusCode == HttpStatus.OK)
    }



    @Test
    fun `create the same expert is unsuccessful`(){
        val newExpert = mockCredentialStaffDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(newExpert, headers)
        val response = restTemplate.postForEntity<Unit>("/API/staff/createExpert", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)

        val responseTwo = restTemplate.exchange<DuplicateEmailException>("/API/staff/createExpert",  HttpMethod.POST, request)
        assert(responseTwo.statusCode == HttpStatus.CONFLICT)
        assertThat(responseTwo.body!!.message == DuplicateEmailException().message)
    }

    @Test
    fun `create expert without password is unsuccessful`(){
        val newExpert = mockCredentialStaffDTO()
        val expertJson = hashMapOf<String, Any>(
            "staffDTO" to newExpert.staffDTO
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)

        val request = HttpEntity(expertJson, headers)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(customerRepository.findByEmailIgnoreCase(newExpert.staffDTO.email) == null)
        assert(keycloak.realm(realmName).users().searchByUsername(mainExpertEmail, true).isEmpty() )
    }




}
