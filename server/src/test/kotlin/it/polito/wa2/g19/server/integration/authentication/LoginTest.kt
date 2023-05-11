package it.polito.wa2.g19.server.integration.authentication


import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.LoginDTO
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g19.server.ticketing.chat.ChatMessageInDTO
import it.polito.wa2.g19.server.ticketing.chat.ChatMessageOutDTO
import it.polito.wa2.g19.server.ticketing.chat.ChatMessageRepository
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusRepository
import it.polito.wa2.g19.server.ticketing.tickets.PriorityLevelRepository
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.*
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.util.LinkedMultiValueMap
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.file.Paths

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoginTest {

    private val prefixEndPoint = "/API/tickets"

    companion object {


        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @Container
        val keycloak = KeycloakContainer("quay.io/keycloak/keycloak:latest")
            .withRealmImportFile("keycloak/realm.json")




        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            val keycloackBaseUrl = keycloak.authServerUrl
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", { "${keycloackBaseUrl}/realms/ticket_management_system" })
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", {"${keycloackBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs"})
        }
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    private lateinit var customer: Customer
    private lateinit var otherCustomer: Customer
    private lateinit var product: Product
    private lateinit var manager: Manager
    private lateinit var expert: Expert
    private lateinit var otherExpert: Expert

    @Autowired
    lateinit var jwtDecoder: JwtDecoder

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
        Util.mockCustomers().forEach {
            if (::customer.isInitialized)
                otherCustomer = customer
            customer = customerRepository.save(it)

        }
        Util.mockManagers().forEach {
            manager = staffRepository.save(it)
        }
        Util.mockExperts().forEach {
            if (::expert.isInitialized)
                otherExpert = expert
            expert = staffRepository.save(it)
        }
        Util.mockPriorityLevels().forEach {
            priorityLevelRepository.save(it)
        }
        product = productRepository.save(Util.mockProduct())
        println("---------------------------------")
    }

    @AfterEach
    fun destroyDatabase() {
        println("----destroying database------")
        attachmentRepository.deleteAll()
        chatRepository.deleteAll()
        ticketStatusRepository.deleteAll()
        ticketRepository.deleteAll()
        priorityLevelRepository.deleteAll()
        productRepository.deleteAll()
        customerRepository.deleteAll()
        staffRepository.deleteAll()
        println("---------------------------------")
    }

    fun insertTicket(status: TicketStatusEnum): Int {
        val ticket = Util.mockTicket()
        ticket.status = status
        ticket.customer = customer
        ticket.product = product
        val ticketStatus = Util.mockOpenTicketStatus()
        ticketStatus.ticket = ticket
        val ticketID = ticketRepository.save(ticket).getId()!!
        ticketStatusRepository.save(ticketStatus)
        return ticketID
    }

    @Test
    fun `login with right credentials is ok`(){
        val loginDTO = LoginDTO("client@test.it", "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        assert(response.statusCode.value() == 200)
        assert(jwtDecoder.decode(response.body).claims.get("email") == loginDTO.username)

    }
}
