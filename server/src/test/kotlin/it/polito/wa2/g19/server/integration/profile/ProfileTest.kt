package it.polito.wa2.g19.server.integration.profile

import com.nimbusds.jose.shaded.gson.Gson
import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.integration.chat.ChatTest
import it.polito.wa2.g19.server.main
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.DuplicateEmailException
import it.polito.wa2.g19.server.profiles.LoginDTO
import it.polito.wa2.g19.server.profiles.customers.CredentialCustomerDTO
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.customers.CustomerDTO
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.staff.*
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g19.server.ticketing.chat.ChatMessageOutDTO
import it.polito.wa2.g19.server.ticketing.chat.ChatMessageRepository
import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevelEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusRepository
import it.polito.wa2.g19.server.ticketing.tickets.PriorityLevelRepository
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
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
import org.springframework.data.repository.findByIdOrNull
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
import org.testcontainers.shaded.okhttp3.Headers
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class ProfileTest {

    private val prefixEndPoint = "/API/profiles"

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

    private lateinit var customer: Customer
    private lateinit var otherCustomer: Customer
    private lateinit var product: Product
    private lateinit var manager: Manager
    private lateinit var expert: Expert
    private lateinit var otherExpert: Expert
    private lateinit var customerToken: String
    private lateinit var expertToken: String
    private lateinit var managerToken: String

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var staffRepository: StaffRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var priorityLevelRepository: PriorityLevelRepository

    @Autowired
    lateinit var ticketStatusRepository: TicketStatusRepository

    @Autowired
    lateinit var chatRepository: ChatMessageRepository

    @Autowired
    lateinit var attachmentRepository: AttachmentRepository


    @BeforeEach
    fun populateDatabase() {
        println("----populating database------")
        manager = Util.mockMainManager()
        manager.id = UUID.randomUUID()
        staffRepository.save(manager)

        println("---------------------------------")
    }

    @AfterEach
    fun destroyDatabase() {
        println("----destroying database------")

        keycloak.realm(realmName).users().search(mainCustomerEmail).let {
            if (it.isEmpty()) return@let null
            val user = it.first()
            keycloak.realm(realmName).users().delete(user.id)
        }
        customerRepository.deleteAll()
        staffRepository.deleteAll()
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
        return StaffDTO(mainExpertEmail, "newCustomerName",
            "newCustomerSurname", StaffType.Expert, listOf("Hardware"))

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

    }

    @Test
    fun `signup and signin is successfful`(){
        val newCustomer = mockCredentialCustomerDTO()
        val request = HttpEntity(newCustomer)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)

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
    fun `signup without password is unsuccesful`(){
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
    fun `create a expert is successfull`(){
        val newExpert = mockCredentialStaffDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(newExpert, headers)
        val response = restTemplate.postForEntity<Unit>("/API/staff/createExpert", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)


    }

    @Test
    fun `create a expert and signin as him is successfull`(){
        val newExpert = mockCredentialStaffDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(newExpert, headers)
        val response = restTemplate.postForEntity<Unit>("/API/staff/createExpert", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)

        val loginResponse: ResponseEntity<String> = restTemplate.exchange("/API/login", HttpMethod.POST, HttpEntity(LoginDTO(newExpert.staffDTO.email, newExpert.password)))
        assert(loginResponse.statusCode == HttpStatus.OK)
    }



    @Test
    fun `create the same expert is unsuccessfull`(){
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
    fun `create expert without password is unsuccesful`(){
        val newExpert = mockCredentialStaffDTO()
        val expertJson = hashMapOf<String, Any>(
            "staffDTO" to newExpert.staffDTO
        )

        val request = HttpEntity(expertJson)
        val response = restTemplate.postForEntity<Unit>("/API/signup", request, HttpMethod.POST)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(customerRepository.findByEmailIgnoreCase(newExpert.staffDTO.email) == null)
        assert(keycloak.realm(realmName).users().searchByUsername(mainExpertEmail, true).isEmpty() )
    }


}
