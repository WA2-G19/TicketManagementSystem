package it.polito.wa2.g19.server.integration.ticketing

import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.equalsTo
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
import it.polito.wa2.g19.server.products.ProductService
import it.polito.wa2.g19.server.profiles.*
import it.polito.wa2.g19.server.ticketing.statuses.*
import it.polito.wa2.g19.server.ticketing.tickets.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
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
class TicketTest {


    private val prefixEndPoint = "/API/tickets"
    companion object {

        lateinit var CustomerRepository: CustomerRepository
        lateinit var ProductRepository: ProductRepository
        lateinit var StaffRepository: StaffRepository



        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @Autowired
        lateinit var ok: Product

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

    lateinit private var customer: Customer
    lateinit private var product: Product
    lateinit private var manager: Manager
    lateinit private var expert: Expert

    private val ticket: Ticket = Util.mockTicket()
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
        Util.mockCustomers().forEach{

            customer = customerRepository.save(it)
        }

        Util.mockManagers().forEach{
            manager = staffRepository.save(it)
        }
        Util.mockExperts().forEach{
             expert =  staffRepository.save(it)
        }
        Util.mockPriorityLevels().forEach{
            priorityLevelRepository.save(it)
        }

        product = productRepository.save(Util.mockProduct())

    }

    @AfterEach
    fun destroyDatabase(){
        ticketStatusRepository.deleteAll()
        priorityLevelRepository.deleteAll()
        ticketRepository.deleteAll()
        productRepository.deleteAll()
        customerRepository.deleteAll()
        staffRepository.deleteAll()
    }

    @Test
    fun openTicket(){
        val newTicket = Util.mockTicketDTO()
        val body = HttpEntity(newTicket)
        val responsePost = restTemplate.postForEntity<Void>(prefixEndPoint, body, HttpMethod.POST)
        assertThat(responsePost.statusCode.value() == 201)
        val location = responsePost.headers.location
        val responseGet = restTemplate.getForEntity(location, TicketOutDTO::class.java )
        val createdTicket = responseGet.body!!
        newTicket.id = createdTicket.id
        assert(newTicket.id == createdTicket.id)
        assert(newTicket.description == createdTicket.description)
        assert(newTicket.customerEmail == createdTicket.customerEmail)
        assert(newTicket.productEan == createdTicket.productEan)


        println(createdTicket.customerEmail)
        println(createdTicket.description)
        println(createdTicket.productEan)
        println(createdTicket.status)
        assert(newTicket.equalsTo(createdTicket))
        assert( createdTicket.priorityLevel == null)
        assert(createdTicket.status == TicketStatusEnum.Open)
        assert(createdTicket.expert == null)


       ticketStatusRepository.findAllByTicketId(createdTicket.id!!).let {
           assert(it.size == 1)
       }
    }

    @Test
    fun `close a open ticket is successful`(){
        val ticket = Util.mockTicket()
        ticket.customer = customer
        ticket.product = product
        val ticketStatus = Util.mockOpenTicketStatus()
        ticketStatus.ticket = ticket
        val ticketID = ticketRepository.save(ticket).getId()!!
        ticketStatusRepository.save(ticketStatus)

        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID",HttpMethod.PUT, body, Void::class.java,)
        assertThat(response.statusCode.value() == 200)

        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)


        val statuses = ticketStatusRepository.findAllByTicketId(ticket.getId()!!)
        assert(statuses.size == 2)
        assertThat((statuses.last() as ClosedTicketStatus).by== manager)
        println(closedTicket.status)
        assert(closedTicket.status == TicketStatusEnum.Closed)




    }










}


