package it.polito.wa2.g19.server.integration.ticketing

import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.LoginDTO
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.statuses.*
import it.polito.wa2.g19.server.ticketing.tickets.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.event.annotation.BeforeTestMethod
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class TicketTest {

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
            registry.add("spring.jpa.hibernate.ddl-auto") {"create-drop"}
            val keycloakBaseUrl = keycloak.authServerUrl
            registry.add("keycloakBaseUrl", { keycloakBaseUrl })
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", { "${keycloakBaseUrl}/realms/ticket_management_system" })
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", {"${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs"})
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



    @BeforeEach
    fun populateDatabase(){
        if(!keycloak.isRunning){
            keycloak.start()
        }
        println("----populating database------")
        Util.mockCustomers().forEach{
            if (::customer.isInitialized)
                otherCustomer = customer
            customer = customerRepository.save(it)
        }
        customer = customerRepository.save(Util.mockMainCustomer())

        Util.mockManagers().forEach{
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
        product = productRepository.save(Util.mockProduct())
        println("---------------------------------")
    }


    @AfterEach
    fun destroyDatabase(){
        println("----destroying database------")
        ticketStatusRepository.deleteAll()
        ticketRepository.deleteAll()
        priorityLevelRepository.deleteAll()
        productRepository.deleteAll()
        customerRepository.deleteAll()
        staffRepository.deleteAll()
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

    @BeforeEach
    fun refreshManagerToken(){
        val loginDTO = LoginDTO(manager.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        managerToken = response.body!!
    }

    fun insertTicket(status: TicketStatusEnum): Int {
        val ticket = Util.mockTicket()
        ticket.status = status
        ticket.expert = expert
        ticket.customer = customer
        ticket.product = product
        val ticketStatus = Util.mockOpenTicketStatus()
        ticketStatus.ticket = ticket
        ticket.statusHistory = mutableSetOf()
        ticket.statusHistory.add(ticketStatus)
        return ticketRepository.save(ticket).getId()!!
    }




    @Test
    fun `open a ticket is successful`(){
        val newTicket = Util.mockTicketDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(newTicket, headers)
        assert(ticketStatusRepository.findAll().size == 0)
        val responsePost = restTemplate.postForEntity<Void>(prefixEndPoint, request, HttpMethod.POST)
        assert(responsePost.statusCode.value() == 201)
        val location = responsePost.headers.location
        val responseGet = restTemplate.exchange(location, HttpMethod.GET, HttpEntity(null, headers),TicketOutDTO::class.java  )
        val createdTicket = responseGet.body!!
        newTicket.id = createdTicket.id
        assert(newTicket.id == createdTicket.id)
        assert(newTicket.description == createdTicket.description)
        println(customer.email)
        println(createdTicket.customerEmail)
        assert(customer.email == createdTicket.customerEmail)
        assert(newTicket.productEan == createdTicket.productEan)
        assert( createdTicket.priorityLevel == null)
        assert(createdTicket.status == TicketStatusEnum.Open)
        assert(createdTicket.expertEmail == null)
        assert(ticketStatusRepository.findAllByTicketId(createdTicket.id!!).size == 1)
    }

    @Test
    fun `open a ticket without bearer`(){
        val newTicket = Util.mockTicketDTO()
        val request = HttpEntity(newTicket, null)
        assert(ticketStatusRepository.findAll().size == 0)
        val responsePost = restTemplate.postForEntity<Void>(prefixEndPoint, request, HttpMethod.POST)
        assert(responsePost.statusCode.value() == 401)
    }

    @Test
    fun `close an open ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers), TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as ClosedTicketStatus).by== manager)
    }

    @Test
    fun `close an open ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `reopen an open ticket is unsuccessful`() {
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID",HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Open, TicketStatusEnum.Reopened).message)
        val openedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers), TicketOutDTO::class.java).body!!
        assert(openedTicket.id == ticketID)
        assert(openedTicket.status == TicketStatusEnum.Open)
        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus is OpenTicketStatus))
    }

    @Test
    fun `reopen an open ticket without bearer`() {
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID",HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `start progress on an open ticket is successful`() {

        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 200)
        val inProgressTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers), TicketOutDTO::class.java).body!!
        assert(inProgressTicket.id == ticketID)
        assert(inProgressTicket.status == TicketStatusEnum.InProgress)
        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as InProgressTicketStatus).expert== expert)
        assert(lastStatus.by == manager)
    }

    @Test
    fun `start progress on an open ticket without bearer`() {

        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `resolve an open ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = manager.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 200)
        val inProgressTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers), TicketOutDTO::class.java).body!!
        assert(inProgressTicket.id == ticketID)
        assert(inProgressTicket.status == TicketStatusEnum.Resolved)
        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as ResolvedTicketStatus).by == manager)
    }

    @Test
    fun `resolve an open ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = manager.email
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `close a in progress ticket is successful`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers), TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as ClosedTicketStatus).by == manager)
    }

    @Test
    fun `close a in progress ticket without bearer`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `reopen a in progress ticket is unsuccessful`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.InProgress, TicketStatusEnum.Reopened).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.InProgress)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `reopen a in progress ticket without bearer`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `resolved a in progress ticket is successful`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Resolved)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as ResolvedTicketStatus).by== expert)
    }

    @Test
    fun `resolved a in progress ticket without bearer`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, Void::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `start progress on an in progress ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.InProgress, TicketStatusEnum.InProgress).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.InProgress)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `start progress on an in progress ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }


    @Test
    fun `start progress on an closed ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Closed, TicketStatusEnum.InProgress).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET, HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)

    }

    @Test
    fun `start progress on an closed ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `close a closed ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Closed, TicketStatusEnum.Closed).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,
            HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `close a closed ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `resolve a closed ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = manager.email,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Closed, TicketStatusEnum.Resolved).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `resolve a closed ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = manager.email,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `reopen a closed ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Reopened)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus is ReopenedTicketStatus))
    }

    @Test
    fun `reopen a closed ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `reopen a reopened ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Reopened, TicketStatusEnum.Reopened).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Reopened)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `reopen a reopened ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `close a reopened ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.status == TicketStatusEnum.Closed)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as ClosedTicketStatus).by== manager)
    }

    @Test
    fun `close a reopened ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `start progress on a reopened ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)

        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.status == TicketStatusEnum.InProgress)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as InProgressTicketStatus).expert== expert)
        assert(lastStatus.by == manager)
    }

    @Test
    fun `start progress on a reopened ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `resolve a resolved ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Resolved, TicketStatusEnum.Resolved).message)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.status == TicketStatusEnum.Resolved)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `resolve a resolved ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `close a resolved ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = expert.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 200)
        headers.setBearerAuth(managerToken)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus as ClosedTicketStatus).by== expert)
    }

    @Test
    fun `reopen a resolved ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Reopened)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 2)
        val lastStatus = ticketStatusRepository.findByTicketAndTimestampIsMaximum(ticketID)
        assert((lastStatus is ReopenedTicketStatus))
    }

    @Test
    fun `start progress on a resolved ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            by = manager.email,
            expert = expert.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Resolved, TicketStatusEnum.InProgress).message)
        val closedTicket =
            restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),TicketOutDTO::class.java).body!!
        assert(closedTicket.status == TicketStatusEnum.Resolved)

        val statuses = ticketStatusRepository.findAllByTicketId(ticketID)
        assert(statuses.size == 1)
    }

    @Test
    fun `get all tickets`(){
        val mySize = 10
        val otherSize = 10
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product; it})
        }
        (0 until otherSize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product; it})
        }
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange(prefixEndPoint, HttpMethod.GET, HttpEntity(null, headers))
        assert(myTicketsDTO.body!!.size == mySize + otherSize )
    }

    @Test
    fun `filtering by customer`(){
        val mySize = 10
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product; it})
        }
        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product; it})
        }
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?customer=${customer.email}", HttpMethod.GET, HttpEntity(null, headers))
        assert(myTicketsDTO.body!!.size == mySize)
        (myTicketsDTO.body!!.forEach{println(it.customerEmail)})
        assert(myTicketsDTO.body!!.all { it.customerEmail == customer.email })
    }



    @Test
    fun `filtering by expert and customer`(){
        val mySize = 10
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = expert;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert;it})
        }

        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it})
        }

        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}", HttpMethod.GET, HttpEntity(null, headers))
        println(myTicketsDTO.body!!.size)
        assert(myTicketsDTO.body!!.size == mySize)
        assert(myTicketsDTO.body!!.all { it.expertEmail == expert.email && it.customerEmail == customer.email })
    }

    @Test
    fun `filtering by status and expert and customer`(){
        val mySize = 10
        val myStatus = TicketStatusEnum.Closed
        val otherStatus = TicketStatusEnum.Reopened
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = myStatus; it.expert = expert;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  myStatus; it.expert = otherExpert;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  otherStatus; it.expert = otherExpert;it})
        }

        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it})
        }
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}&status=${myStatus}", HttpMethod.GET, HttpEntity(null, headers))
        assert(myTicketsDTO.body!!.size == mySize)
        assert(myTicketsDTO.body!!.all { it.expertEmail == expert.email && it.customerEmail == customer.email && it.status == myStatus })
    }

    @Test
    fun `filtering by priority and status and expert and customer`(){
        val mySize = 10
        val myStatus = TicketStatusEnum.Closed
        val otherStatus = TicketStatusEnum.Reopened

        val myPriorityLevel = priorityLevelRepository.findByName("HIGH")
        val otherPriorityLevel = priorityLevelRepository.findByName("LOW")
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = myStatus; it.expert = expert; it.priorityLevel = myPriorityLevel; it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  myStatus; it.expert = otherExpert;it.priorityLevel = myPriorityLevel;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  otherStatus; it.expert = otherExpert;it.priorityLevel = myPriorityLevel;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  otherStatus; it.expert = otherExpert;it.priorityLevel = otherPriorityLevel;it})
        }

        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it.priorityLevel = otherPriorityLevel; it})
        }

        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange(
            "$prefixEndPoint?expert=${expert.email}&customer=${customer.email}&status=${myStatus}&priorityLevel=${myPriorityLevel.name}",
            HttpMethod.GET,
            HttpEntity(null, headers)
        )
        assert(myTicketsDTO.body!!.size == mySize)
        assert(myTicketsDTO.body!!.all {
            it.expertEmail == expert.email && it.customerEmail == customer.email
                    && it.status == myStatus && it.priorityLevel == PriorityLevelEnum.valueOf(myPriorityLevel.name)
        })

    }

    @Test
    fun `try to close a ticket without by indication`(){

        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProfileNotFoundException::class.java,
        )
        assert(response.statusCode.value() == 400)
        assert(ProfileNotFoundException().message == response.body!!.message)
    }

    @Test
    fun `try to start progress on an open ticket is not successful without by and expert indication`() {
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProfileNotFoundException::class.java,
        )
        assert(response.statusCode.value() == 400)
        assert(ProfileNotFoundException().message == response.body!!.message)
    }

    @Test
    fun `try to start progress on an open ticket is not successful without 'by' indication`() {
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProfileNotFoundException::class.java,
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.message!! == ProfileNotFoundException().message)
    }

    @Test
    fun `try to start progress on an open ticket is not successful without any indication`() {
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProfileNotFoundException::class.java,
        )
        assert(response.statusCode.value() == 400)
        assert(response.body!!.message!! == ProfileNotFoundException().message)
    }

    @Test
    fun `close a ticket that does not exist`() {
        val ticketID = 10
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java,
        )
        assert(response.statusCode.value() == 404)
        assert(response.body!!.detail == TicketNotFoundException().message!!)
    }



    @Test
    fun `close a resolved ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = expert.email
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `reopen a resolved ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `start progress on a resolved ticket without bearer`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            by = manager.email,
            expert = expert.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java
        )
        assert(response.statusCode.value() == 401)
    }

    @Test
    fun `get all tickets without bearer`(){
        val mySize = 10
        val otherSize = 10
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product; it})
        }
        (0 until otherSize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product; it})
        }
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange(prefixEndPoint, HttpMethod.GET, HttpEntity(null, null))
        assert(myTicketsDTO.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `filtering by customer without bearer`(){
        val mySize = 10
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product; it})
        }
        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product; it})
        }
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?customer=${customer.email}", HttpMethod.GET, HttpEntity(null, null))
        assert(myTicketsDTO.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `filtering by expert and customer without bearer`(){
        val mySize = 10
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = expert;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert;it})
        }

        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it})
        }

        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}", HttpMethod.GET, HttpEntity(null, null))
        assert(myTicketsDTO.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `filtering by status and expert and customer without bearer`(){
        val mySize = 10
        val myStatus = TicketStatusEnum.Closed
        val otherStatus = TicketStatusEnum.Reopened
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = myStatus; it.expert = expert;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  myStatus; it.expert = otherExpert;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  otherStatus; it.expert = otherExpert;it})
        }

        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it})
        }
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}&status=${myStatus}", HttpMethod.GET, HttpEntity(null, null))
        assert(myTicketsDTO.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `filtering by priority and status and expert and customer without bearer`(){
        val mySize = 10
        val myStatus = TicketStatusEnum.Closed
        val otherStatus = TicketStatusEnum.Reopened

        val myPriorityLevel = priorityLevelRepository.findByName("HIGH")
        val otherPriorityLevel = priorityLevelRepository.findByName("LOW")
        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status = myStatus; it.expert = expert; it.priorityLevel = myPriorityLevel; it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  myStatus; it.expert = otherExpert;it.priorityLevel = myPriorityLevel;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  otherStatus; it.expert = otherExpert;it.priorityLevel = myPriorityLevel;it})
        }

        (0 until mySize).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product
                it.status =  otherStatus; it.expert = otherExpert;it.priorityLevel = otherPriorityLevel;it})
        }

        (0 until 12).forEach{ _ ->
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it.priorityLevel = otherPriorityLevel; it})
        }

        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange(
            "$prefixEndPoint?expert=${expert.email}&customer=${customer.email}&status=${myStatus}&priorityLevel=${myPriorityLevel.name}",
            HttpMethod.GET,
            HttpEntity(null, null)
        )
        assert(myTicketsDTO.statusCode == HttpStatus.UNAUTHORIZED)

    }

    @Test
    fun `close a ticket that does not exist without bearer`() {
        val ticketID = 10
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val request = HttpEntity(ticketStatusDTO, null)
        val response = restTemplate.exchange(
            "$prefixEndPoint/$ticketID",
            HttpMethod.PUT,
            request,
            ProblemDetail::class.java,
        )
        assert(response.statusCode.value() == 401)
    }


    @Test
    fun `manager cannot open a ticket `(){
        val newTicket = Util.mockTicketDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(newTicket, headers)
        val responsePost = restTemplate.postForEntity<Void>(prefixEndPoint, request, HttpMethod.POST)
        assert(responsePost.statusCode.value() == 403)

    }

    @Test
    fun `expert cannot open a ticket`(){
        val newTicket = Util.mockTicketDTO()
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(newTicket, headers)
        val responsePost = restTemplate.postForEntity<Void>(prefixEndPoint, request, HttpMethod.POST)
        assert(responsePost.statusCode.value() == 403)
    }

    @Test
    fun `customer cannot get a ticket of another customer`(){
        val ticket = Util.mockTicket()
        ticket.product = product
        ticket.customer = otherCustomer
        ticket.status = TicketStatusEnum.Open
        val ticketID = ticketRepository.save(ticket).getId()
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),ProblemDetail::class.java)
        assert(response.statusCode.value() == 404)
        assert(response.body!!.detail == TicketNotFoundException().message!!)
    }


    @Test
    fun `customer  gets only his  tickets`(){
        val ticket = Util.mockTicket()
        ticket.product = product
        ticket.customer = otherCustomer
        ticket.status = TicketStatusEnum.Open
        val ticketID = ticketRepository.save(ticket).getId()
        insertTicket(TicketStatusEnum.Open)

        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val response: ResponseEntity<List<TicketOutDTO>>  = restTemplate.exchange("$prefixEndPoint", HttpMethod.GET,HttpEntity(null, headers))
        assert( response.body!!.size == 1)
        response.body!!.forEach{
            assert(it.customerEmail == customer.email)
        }
    }

    @Test
    fun `expert  gets only his  tickets`(){
        val ticket = Util.mockTicket()
        ticket.product = product
        ticket.customer = otherCustomer
        ticket.expert = otherExpert
        ticket.status = TicketStatusEnum.Open
        val ticketID = ticketRepository.save(ticket).getId()
        insertTicket(TicketStatusEnum.Open)

        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val response: ResponseEntity<List<TicketOutDTO>>  = restTemplate.exchange("$prefixEndPoint", HttpMethod.GET,HttpEntity(null, headers))
        assert( response.body!!.size == 1)
        response.body!!.forEach{
            assert(it.expertEmail == expert.email)
        }
    }

    @Test
    fun `expert cannot get a ticket of another expert`(){
        val ticket = Util.mockTicket()
        ticket.expert = otherExpert
        ticket.product = product
        ticket.customer = otherCustomer
        ticket.status = TicketStatusEnum.Open
        val ticketID = ticketRepository.save(ticket).getId()
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.GET,HttpEntity(null, headers),ProblemDetail::class.java)
        assert(response.statusCode.value() == 404)
        assert(response.body!!.detail == TicketNotFoundException().message!!)
    }

    @Test
    fun `expert cannot reopen a ticket`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    @Test
    fun `manager cannot reopen a ticket`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    @Test
    fun `customer cannot resolve a ticket`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    @Test
    fun `customer cannot close a ticket`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by=manager.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    @Test
    fun `customer cannot start progress a ticket`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,

            TicketStatusEnum.InProgress,
            by = manager.email,
            expert = expert.email,
            priorityLevel = PriorityLevelEnum.CRITICAL

        )
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.FORBIDDEN)
    }

    @Test
    fun `resolve an open ticket is unsuccessful for another expert`(){

        val ticket = Util.mockTicket()
        ticket.status = TicketStatusEnum.Open
        ticket.expert = otherExpert
        ticket.customer = customer
        ticket.product = product
        val ticketStatus = Util.mockOpenTicketStatus()
        ticketStatus.ticket = ticket
        ticket.statusHistory = mutableSetOf()
        ticket.statusHistory.add(ticketStatus)
        val ticketID = ticketRepository.save(ticket).getId()

        val ticketStatusDTO = TicketStatusDTO(
            ticketID!!,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body!!.detail!! == TicketNotFoundException().message)

    }

    @Test
    fun `close an open ticket is unsuccessful for another expert`(){

        val ticket = Util.mockTicket()
        ticket.status = TicketStatusEnum.Resolved
        ticket.expert = otherExpert
        ticket.customer = customer
        ticket.product = product
        val ticketStatus = Util.mockOpenTicketStatus()
        ticketStatus.ticket = ticket
        ticket.statusHistory = mutableSetOf()
        ticket.statusHistory.add(ticketStatus)
        val ticketID = ticketRepository.save(ticket).getId()

        val ticketStatusDTO = TicketStatusDTO(
            ticketID!!,
            TicketStatusEnum.Closed,
            by = expert.email
        )
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = HttpEntity(ticketStatusDTO, headers)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, request, ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body!!.detail == TicketNotFoundException().message)

    }


}


