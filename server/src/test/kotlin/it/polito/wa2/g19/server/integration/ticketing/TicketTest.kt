package it.polito.wa2.g19.server.integration.ticketing

import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.equalsTo
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
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
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
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

    lateinit private var customer: Customer
    lateinit private var otherCustomer: Customer
    lateinit private var product: Product
    lateinit private var manager: Manager
    lateinit private var expert: Expert
    lateinit private var otherExpert: Expert

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
        println("----populating database------")
        Util.mockCustomers().forEach{
            if (::customer.isInitialized)
                otherCustomer = customer
            customer = customerRepository.save(it)

        }

        Util.mockManagers().forEach{
            manager = staffRepository.save(it)
        }
        Util.mockExperts().forEach{
            if(::expert.isInitialized)
                otherExpert = expert
            expert =  staffRepository.save(it)
        }
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
    fun `open a ticket is successful`(){
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
        assert(newTicket.equalsTo(createdTicket))
        assert( createdTicket.priorityLevel == null)
        assert(createdTicket.status == TicketStatusEnum.Open)
        assert(createdTicket.expertEmail == null)
        ticketStatusRepository.findAllByTicketId(createdTicket.id!!).let {
           assert(it.size == 1)
        }
    }

    @Test
    fun `close an open ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Open)
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
        assert(closedTicket.status == TicketStatusEnum.Closed)
        //val statuses = ticketStatusRepository.findAllByTicketId(ticketID)!!

        //assert(statuses.size == 2)
        //assertThat((statuses.last() as ClosedTicketStatus).by== manager)
    }

    @Test
    fun `reopen an open ticket is unsuccessful`() {
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID",HttpMethod.PUT, body, ProblemDetail::class.java)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Open, TicketStatusEnum.Reopened).message)
        val openedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(openedTicket.id == ticketID)
        assert(openedTicket.status == TicketStatusEnum.Open)
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
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, Void::class.java,)
        assert(response.statusCode.value() == 200)
        val inProgressTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(inProgressTicket.id == ticketID)
        assert(inProgressTicket.status == TicketStatusEnum.InProgress)
    }

    @Test
    fun `resolve an open ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Open)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, Void::class.java,)
        assert(response.statusCode.value() == 200)
        val inProgressTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(inProgressTicket.id == ticketID)
        assert(inProgressTicket.status == TicketStatusEnum.Resolved)
    }


    @Test
    fun `close a in progress ticket is successful`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, Void::class.java,)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
    }

    @Test
    fun `reopen a in progress ticket is unsuccessful`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.InProgress, TicketStatusEnum.Reopened).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.InProgress)
    }

    @Test
    fun `resolved a in progress ticket is successful`() {
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, Void::class.java,)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Resolved)
    }

    @Test
    fun `start progress on an in progress ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.InProgress)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.InProgress,
            expert = expert.email,
            by = manager.email,
            priorityLevel = PriorityLevelEnum.CRITICAL
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.InProgress, TicketStatusEnum.InProgress).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.InProgress)
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
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Closed, TicketStatusEnum.InProgress).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
    }

    @Test
    fun `close a closed ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Closed, TicketStatusEnum.Closed).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
    }

    @Test
    fun `resolve a closed ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = manager.email,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Closed, TicketStatusEnum.Resolved).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
    }

    @Test
    fun `reopen a closed ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Closed)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Reopened)
    }

    @Test
    fun `reopen a reopened ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Reopened, TicketStatusEnum.Reopened).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Reopened)
    }

    @Test
    fun `close a reopened ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Reopened)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = manager.email
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
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
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.InProgress)
    }

    @Test
    fun `resolve a resolved ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Resolved,
            by = expert.email
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Resolved, TicketStatusEnum.Resolved).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Resolved)
    }

    @Test
    fun `close a resolved ticket is successful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Closed,
            by = expert.email
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Closed)
    }

    @Test
    fun `reopen a resolved ticket is unsuccessful`(){
        val ticketID = insertTicket(TicketStatusEnum.Resolved)
        val ticketStatusDTO = TicketStatusDTO(
            ticketID,
            TicketStatusEnum.Reopened,
        )
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java)
        assert(response.statusCode.value() == 200)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Reopened)
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
        val body = HttpEntity(ticketStatusDTO)
        val response = restTemplate.exchange("$prefixEndPoint/$ticketID", HttpMethod.PUT, body, ProblemDetail::class.java,)
        assert(response.statusCode.value() == 400)
        assert(response.body!!.detail == InvalidTicketStatusTransitionException(TicketStatusEnum.Resolved, TicketStatusEnum.InProgress).message)
        val closedTicket = restTemplate.getForEntity("$prefixEndPoint/$ticketID", TicketOutDTO::class.java).body!!
        assert(closedTicket.id == ticketID)
        assert(closedTicket.status == TicketStatusEnum.Resolved)
    }

    @Test
    fun `get all tickets`(){
        val mySize = 10
        val otherSize = 10
        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product; it})
        }
        (0 until otherSize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product; it})
        }

        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint", HttpMethod.GET, null)
        assert(myTicketsDTO.body!!.size == mySize + otherSize )
    }

    @Test
    fun `filtering by customer`(){
        val mySize = 10
        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product; it})
        }
        (0 until 12).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product; it})
        }

        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?customer=${customer.email}", HttpMethod.GET, null)
        assert(myTicketsDTO.body!!.size == mySize)
        (myTicketsDTO.body!!.forEach{println(it.customerEmail)})
        assert(myTicketsDTO.body!!.all { it.customerEmail == customer.email })
    }



    @Test
    fun `filtering by expert and customer`(){
        val mySize = 10
        (0 until mySize).forEach{


            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status = TicketStatusEnum.InProgress; it.expert = expert;it})
        }

        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert;it})
        }

        (0 until 12).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product;
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it})
        }

        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}", HttpMethod.GET, null)
        println(myTicketsDTO.body!!.size)
        assert(myTicketsDTO.body!!.size == mySize)
        assert(myTicketsDTO.body!!.all { it.expertEmail == expert.email && it.customerEmail == customer.email })
    }

    @Test
    fun `filtering by status and expert and customer`(){
        val mySize = 10
        val myStatus = TicketStatusEnum.Closed
        val otherStatus = TicketStatusEnum.Reopened
        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status = myStatus; it.expert = expert;it})
        }

        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status =  myStatus; it.expert = otherExpert;it})
        }

        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status =  otherStatus; it.expert = otherExpert;it})
        }

        (0 until 12).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product;
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it})
        }
        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}&status=${myStatus}", HttpMethod.GET, null)
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
        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status = myStatus; it.expert = expert; it.priorityLevel = myPriorityLevel; it})
        }

        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status =  myStatus; it.expert = otherExpert;it.priorityLevel = myPriorityLevel;it})
        }

        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status =  otherStatus; it.expert = otherExpert;it.priorityLevel = myPriorityLevel;it})
        }

        (0 until mySize).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = customer; it.product = product;
                it.status =  otherStatus; it.expert = otherExpert;it.priorityLevel = otherPriorityLevel;it})
        }

        (0 until 12).forEach{
            ticketRepository.save(Util.mockTicket().let { it.customer = otherCustomer; it.product = product;
                it.status = TicketStatusEnum.InProgress; it.expert = otherExpert; it.priorityLevel = otherPriorityLevel; it})
        }


        val myTicketsDTO: ResponseEntity<List<TicketOutDTO>> = restTemplate.exchange("$prefixEndPoint?expert=${expert.email}&customer=${customer.email}&status=${myStatus}&priorityLevel=${myPriorityLevel.name}", HttpMethod.GET, null)
        assert(myTicketsDTO.body!!.size == mySize)
        assert(myTicketsDTO.body!!.all { it.expertEmail == expert.email && it.customerEmail == customer.email
                && it.status == myStatus && it.priorityLevel == PriorityLevelEnum.valueOf(myPriorityLevel.name) })
    }



}


