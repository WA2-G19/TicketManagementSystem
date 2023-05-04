package it.polito.wa2.g19.server.integration.stats

import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.staff.Expert
import it.polito.wa2.g19.server.profiles.staff.Manager
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g19.server.ticketing.chat.ChatMessageRepository
import it.polito.wa2.g19.server.ticketing.statuses.ClosedTicketStatus
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusDTO
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusRepository
import it.polito.wa2.g19.server.ticketing.tickets.PriorityLevelRepository
import it.polito.wa2.g19.server.ticketing.tickets.TicketOutDTO
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StatsTest {

    private val prefixEndPoint = "/API/tickets"
    private val prefixStatsEndPoint = "/API/stats"

    companion object {


        @Container
        val postgres = PostgreSQLContainer("postgres:latest")


        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
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
    fun `get count for closed tickets by expert`() {
        for (i in 0 until 20) {
            val ticketID = insertTicket(TicketStatusEnum.Open)
            val ticketStatusDTO = TicketStatusDTO(
                ticketID,
                TicketStatusEnum.Closed,
                by = expert.email,
            )
            val ticket = ticketRepository.findByIdOrNull(ticketID)!!
            ticketStatusRepository.save(ClosedTicketStatus().apply {
                this.ticket = ticket
                this.by = expert
            })
            val body = HttpEntity(ticketStatusDTO)
            val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, Void::class.java)
            assert(response.statusCode.value() == 200)
            println(response)
            val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
            assert(closedTicket.id == ticketID)
            assert(closedTicket.status == TicketStatusEnum.Closed)
        }
        val response = restTemplate.exchange<Int>("$prefixStatsEndPoint/ticketsClosed/${expert.email}", HttpMethod.GET, null)
        assert(response.body == 20)
    }

    @Test
    fun `get closed tickets is unsuccessful`() {
        val response = restTemplate.getForEntity("$prefixStatsEndPoint/ticketsClosed/fakeprofile@test.it",ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body!!.detail == ProfileNotFoundException().message)
    }

    @Test
    fun `get averege time for closed tickets by expert`() {
        for (i in 0 until 20) {
            val ticketID = insertTicket(TicketStatusEnum.Open)
            val ticketStatusDTO = TicketStatusDTO(
                ticketID,
                TicketStatusEnum.Closed,
                by = expert.email,
                timestamp = LocalDateTime.now().plusDays((i).toLong())
            )
            val ticket = ticketRepository.findByIdOrNull(ticketID)!!
            val ticketStatus = ticketStatusRepository.save(ClosedTicketStatus().apply {
                this.ticket = ticket
                this.by = expert
            })
            ticketStatus.timestamp = LocalDateTime.now().plusDays((1).toLong())
            ticketStatusRepository.save(ticketStatus)
            val body = HttpEntity(ticketStatusDTO)
            val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, Void::class.java)
            assert(response.statusCode.value() == 200)
            println(response)
            val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
            assert(closedTicket.id == ticketID)
            assert(closedTicket.status == TicketStatusEnum.Closed)
        }
        val response = restTemplate.exchange<Int>("$prefixStatsEndPoint/averageTime/${expert.email}", HttpMethod.GET, null)
        assert(response.body!! == 1*24*3600)
    }

    @Test
    fun `get average time is unsuccessful`() {
        val response = restTemplate.getForEntity("$prefixStatsEndPoint/averageTime/fakeprofile@test.it",ProblemDetail::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body!!.detail == ProfileNotFoundException().message)
    }
}