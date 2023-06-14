package it.polito.wa2.g19.server.integration.stats

import it.polito.wa2.g19.server.Util
import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.wa2.g19.server.integration.ticketing.TicketTest
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.LoginDTO
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.vendors.Vendor
import it.polito.wa2.g19.server.repositories.jpa.*
import it.polito.wa2.g19.server.ticketing.statuses.*
import it.polito.wa2.g19.server.ticketing.tickets.*
import it.polito.wa2.g19.server.warranty.Warranty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class StatsTest {

    private val prefixStatsEndPoint = "/API/stats"

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
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
            val keycloakBaseUrl = keycloak.authServerUrl
            registry.add("keycloakBaseUrl") { keycloakBaseUrl }
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { "${keycloakBaseUrl}/realms/ticket_management_system" }
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri") {"${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs"}
        }
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    private lateinit var customer: Customer
    private lateinit var otherCustomer: Customer
    private lateinit var expert: Expert
    private lateinit var otherExpert: Expert
    private lateinit var manager: Manager
    private lateinit var vendor: Vendor



    private lateinit var customerToken: String
    private lateinit var expertToken: String
    private lateinit var managerToken: String

    private lateinit var warranty: Warranty
    private lateinit var otherWarranty: Warranty
    private lateinit var expiredWarranty: Warranty
    private lateinit var notActivatedWarranty: Warranty
    private lateinit var product: Product


    @Autowired
    lateinit var customerRepository: CustomerRepository
    @Autowired
    lateinit var staffRepository: StaffRepository
    @Autowired
    lateinit var vendorRepository: VendorRepository
    @Autowired
    lateinit var productRepository: ProductRepository
    @Autowired
    lateinit var ticketRepository: TicketRepository
    @Autowired
    lateinit var priorityLevelRepository: PriorityLevelRepository
    @Autowired
    lateinit var ticketStatusRepository: TicketStatusRepository
    @Autowired
    lateinit var warrantyRepository: WarrantyRepository




    @BeforeEach
    fun populateDatabase(){
        if(!TicketTest.keycloak.isRunning){
            TicketTest.keycloak.start()
        }
        println("----populating database------")
        Util.mockCustomers().forEach{
            if (::customer.isInitialized)
                otherCustomer = customer
            customer = customerRepository.save(it)
        }

        customer = customerRepository.save(Util.mockMainCustomer())

        Util.mockManagers().forEach{
            it.id = UUID.randomUUID()
            manager = staffRepository.save(it)
        }
        manager = staffRepository.save(Util.mockMainManager())

        Util.mockExperts().forEach{
            if(::expert.isInitialized)
                otherExpert = expert
            expert =  staffRepository.save(it)
        }

        expert = staffRepository.save(Util.mockMainExpert())
        Util.mockPriorityLevels().forEach{
            priorityLevelRepository.save(it)
        }
        vendor = vendorRepository.save(Util.mockVendor())
        product = productRepository.save(Util.mockProduct())
        warranty = warrantyRepository.save(Util.mockWarranty(product, vendor, customer))
        expiredWarranty = warrantyRepository.save(Util.mockExpiredWarranty(product, vendor, customer))
        notActivatedWarranty = warrantyRepository.save(Util.mockNotActivatedWarranty(product, vendor, customer))
        otherWarranty = warrantyRepository.save(Util.mockWarranty(product, vendor, otherCustomer))

        Util.warrantyUUID = warranty.id!!
        println("---------------------------------")
    }


    @AfterEach
    fun destroyDatabase(){
        println("----destroying database------")
        ticketStatusRepository.deleteAll()
        ticketRepository.deleteAll()
        warrantyRepository.deleteAll()

        priorityLevelRepository.deleteAll()
        productRepository.deleteAll()
        customerRepository.deleteAll()
        staffRepository.deleteAll()
        vendorRepository.deleteAll()
        println("---------------------------------")

    }

    @BeforeEach
    fun refreshCustomerToken(){
        val loginDTO = LoginDTO(customer.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        customerToken = response.body!!
    }

    @BeforeEach
    fun refreshExpertToken(){
        val loginDTO = LoginDTO(expert.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        expertToken = response.body!!
    }





    fun insertTicket(status: TicketStatusEnum): Ticket {
        val ticket = Util.mockTicket()
        ticket.status = status
        ticket.warranty = warranty
        val ticketStatus = Util.mockInProgressTicketStatus()
        ticketStatus.priority = priorityLevelRepository.findByIdOrNull(PriorityLevelEnum.HIGH.name)!!
        ticketStatus.ticket = ticket
        ticketStatus.expert = expert
        ticket.statusHistory = mutableSetOf()
        ticket.statusHistory.add(ticketStatus)
        return ticketRepository.save(ticket)
    }

    @BeforeEach
    fun refreshManagerToken(){
        val loginDTO = LoginDTO(manager.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        managerToken = response.body!!
    }

    @Test
    fun `get count for closed tickets by expert`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        for (i in 0 until 20) {
            val ticket = insertTicket(TicketStatusEnum.Open)
            ticket.statusHistory.add(ClosedTicketStatus().apply {
                this.ticket = ticket
                this.by = expert

            })
            ticketRepository.save(ticket)
        }
        val response = restTemplate.exchange("$prefixStatsEndPoint/tickets-closed/${expert.email}", HttpMethod.GET, HttpEntity(null, headers), Int::class.java)
        assert(response.body == 20)
    }

    @Test
    fun `get closed tickets is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val response = restTemplate.exchange(
            "$prefixStatsEndPoint/tickets-closed/fakeprofile@test.it",HttpMethod.GET,HttpEntity(null,headers),ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body!!.detail == ProfileNotFoundException().message)
    }

    @Test
    fun `test privacy of tickets closed`() {
        val response = restTemplate.exchange(
            "$prefixStatsEndPoint/tickets-closed/${expert.email}",HttpMethod.GET,HttpEntity(null,null),ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `get with invalid token of tickets closed is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth("test")
        val response = restTemplate.exchange(
            "$prefixStatsEndPoint/tickets-closed/${expert.email}",HttpMethod.GET,HttpEntity(null,headers),ProblemDetail::class.java)
      assert(response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `get average time for closed tickets by expert`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        for (i in 0 until 20) {
            val ticket = insertTicket(TicketStatusEnum.InProgress)
            val ticketStatus = ticketStatusRepository.save(ClosedTicketStatus().apply {
                this.ticket = ticket
                this.by = expert
            })
            ticket.statusHistory.add(ticketStatusRepository.save(ticketStatus.let {
                it.timestamp = LocalDateTime.now().plusDays((1).toLong())
                it
            }))
            ticketRepository.save(ticket)
        }
        println("---------------")
        val response =
            restTemplate.exchange<Int>("$prefixStatsEndPoint/average-time/${expert.email}", HttpMethod.GET, HttpEntity(null,headers))
        println(response.body)
        assert(response.body!! == 1 * 24 * 3600)
    }

    @Test
    fun `get average time is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val response = restTemplate.exchange(
            "$prefixStatsEndPoint/average-time/fakeprofile@test.it",HttpMethod.GET,HttpEntity(null,headers),
            ProblemDetail::class.java
        )
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body!!.detail == ProfileNotFoundException().message)
    }

    @Test
    fun `test privacy of average time`() {
        for (i in 0 until 20) {
            val ticket = insertTicket(TicketStatusEnum.InProgress)
            val ticketStatus = ticketStatusRepository.save(ClosedTicketStatus().apply {
                this.ticket = ticket
                this.by = expert
            })
            ticket.statusHistory.add(ticketStatusRepository.save(ticketStatus.let {
                it.timestamp = LocalDateTime.now().plusDays((1).toLong())
                it
            }))
            ticketRepository.save(ticket)
        }
        println("---------------")
        val response =
            restTemplate.exchange<Int>("$prefixStatsEndPoint/average-time/${expert.email}", HttpMethod.GET, HttpEntity(null,null))
        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `get with invalid token of average time is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth("test")
        for (i in 0 until 20) {
            val ticket = insertTicket(TicketStatusEnum.InProgress)
            val ticketStatus = ticketStatusRepository.save(ClosedTicketStatus().apply {
                this.ticket = ticket
                this.by = expert
            })
            ticket.statusHistory.add(ticketStatusRepository.save(ticketStatus.let {
                it.timestamp = LocalDateTime.now().plusDays((1).toLong())
                it
            }))
            ticketRepository.save(ticket)
        }
        println("---------------")
        val response =
            restTemplate.exchange<Int>("$prefixStatsEndPoint/average-time/${expert.email}", HttpMethod.GET, HttpEntity(null,headers))
        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
        }
}