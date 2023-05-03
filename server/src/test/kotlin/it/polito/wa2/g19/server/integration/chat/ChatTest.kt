package it.polito.wa2.g19.server.integration.chat

import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.products.ProductRepository
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
import it.polito.wa2.g19.server.ticketing.tickets.Ticket
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.util.LinkedMultiValueMap
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class ChatTest {

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

    private lateinit var customer: Customer
    private lateinit var otherCustomer: Customer
    private lateinit var product: Product
    private lateinit var manager: Manager
    private lateinit var expert: Expert
    private lateinit var otherExpert: Expert

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
    @Autowired
    lateinit var chatRepository: ChatMessageRepository
    @Autowired
    lateinit var attachmentRepository: AttachmentRepository

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
    fun `get all messages for a ticket`() {
        val ticketId = insertTicket(TicketStatusEnum.Open)
        val messageBody = "This is a test message"
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(customer.email, messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet: ResponseEntity<Set<ChatMessageOutDTO>> = restTemplate.exchange("$prefixEndPoint/$ticketId/chat-messages", HttpMethod.GET, null)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.size == 1)
        assert(responseGet.body!!.elementAt(0).authorEmail == customer.email)
        assert(responseGet.body!!.elementAt(0).body == messageBody)
    }

    @Test
    fun `get all messages for a non existent ticket`() {
        val responseGet: ResponseEntity<ProblemDetail> = restTemplate.exchange("$prefixEndPoint/1/chat-messages", HttpMethod.GET, null)
        assert(responseGet.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `create a simple message for a ticket`() {
        val ticketId = insertTicket(TicketStatusEnum.Open)
        val messageBody = "This is a test message"
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(customer.email, messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet = restTemplate.getForEntity(response.headers.location, ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
    }

    @Test
    fun `create a message for a ticket with attachments`() {
        val ticketId = insertTicket(TicketStatusEnum.Open)
        val messageBody = "This is a test message"
        val file1Content = "test".toByteArray()
        val file2Content = "test2".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(customer.email, messageBody))
                add("files", object : ByteArrayResource(file1Content) {
                    override fun getFilename(): String = "test.txt"
                })
                add("files", object : ByteArrayResource(file2Content) {
                    override fun getFilename(): String = "test2.txt"
                })
           })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet = restTemplate.getForEntity(response.headers.location, ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 2)
    }

    @Test
    fun `create a message for a ticket with attachments and get the attachments`() {
        val ticketId = insertTicket(TicketStatusEnum.Open)
        val messageBody = "This is a test message"
        val fileName = "test.txt"
        val fileContent = "test".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(customer.email, messageBody))
                add("files", object : ByteArrayResource(fileContent) {
                    override fun getFilename(): String = fileName
                })})
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet = restTemplate.getForEntity(response.headers.location, ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 1)
        val responseGetAttachment = restTemplate.getForEntity(responseGet.body!!.stubAttachments!!.elementAt(0).url, ByteArrayResource::class.java)
        assert(responseGetAttachment.statusCode == HttpStatus.OK)
        assert(responseGetAttachment.headers.contentType == MediaType.TEXT_PLAIN)
        assert(responseGetAttachment.headers.contentDisposition.isAttachment)
        assert(responseGetAttachment.headers.contentDisposition.filename == fileName)
        assert(responseGetAttachment.body!!.byteArray.contentEquals(fileContent))
    }
}