package it.polito.wa2.g19.server.integration.chat

/*
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatTest {

    private val prefixEndPoint = "/API/tickets"

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

    @Autowired
    lateinit var chatRepository: ChatMessageRepository

    @Autowired
    lateinit var attachmentRepository: AttachmentRepository

    @BeforeEach
    fun populateDatabase(){
        if(!TicketTest.keycloak.isRunning){
            TicketTest.keycloak.start()
        }
        println("----populating database------")
        Util.mockCustomers().forEach{
            println(it.email)
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
        attachmentRepository.deleteAll()
        chatRepository.deleteAll()
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

    @BeforeEach
    fun refreshManagerToken(){
        val loginDTO = LoginDTO(manager.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        managerToken = response.body!!
    }

    fun insertTicket(): Int {
        val ticket = Util.mockTicket()
        ticket.status = TicketStatusEnum.Open
        ticket.warranty = warranty
        val ticketStatus = Util.mockOpenTicketStatus()
        ticketStatus.ticket = ticket
        ticket.statusHistory = mutableSetOf()
        ticket.statusHistory.add(ticketStatus)
        val ticketID = ticketRepository.save(ticket).getId()!!
        ticketStatusRepository.save(ticketStatus)
        return ticketID
    }

    fun assignTicket(ticketId: Int, expert: Expert, by: Manager) {
        val ticket = ticketRepository.findByIdOrNull(ticketId)!!
        ticket.status = TicketStatusEnum.InProgress
        ticket.expert = expert
        val ticketStatus = Util.mockInProgressTicketStatus()
        ticketStatus.priority = priorityLevelRepository.findByIdOrNull(PriorityLevelEnum.HIGH.name)!!
        ticketStatus.ticket = ticket
        ticketStatus.expert = expert
        ticketStatus.by = manager
        ticket.statusHistory = mutableSetOf()
        ticket.statusHistory.add(ticketStatus)
        ticketRepository.save(ticket)
        ticketStatusRepository.save(ticketStatus)
    }

    @Test
    fun `get all messages for a ticket as a Customer`() {
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet: ResponseEntity<Set<ChatMessageOutDTO>> =
            restTemplate.exchange("$prefixEndPoint/$ticketId/chat-messages", HttpMethod.GET, HttpEntity(null, headers))
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.size == 1)
        assert(responseGet.body!!.elementAt(0).authorEmail == customer.email)
        assert(responseGet.body!!.elementAt(0).body == messageBody)
    }

    @Test
    fun `get all messages for a ticket as an Expert`() {
        val ticketId = insertTicket()
        assignTicket(ticketId, expert, manager)
        val messageBody = "This is a test message"
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet: ResponseEntity<Set<ChatMessageOutDTO>> =
            restTemplate.exchange("$prefixEndPoint/$ticketId/chat-messages", HttpMethod.GET, HttpEntity(null, headers))
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.size == 1)
        assert(responseGet.body!!.elementAt(0).authorEmail == expert.email)
        assert(responseGet.body!!.elementAt(0).body == messageBody)
    }

    @Test
    fun `get messages for a not assigned ticket as an Expert`() {
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val headers1 = HttpHeaders()
        headers1.setBearerAuth(customerToken)
        val request1 = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
            })
        val response1 = restTemplate.exchange<Void>(request1)
        assert(response1.statusCode == HttpStatus.CREATED)
        assert(response1.headers.location.toString().isNotBlank())
        val headers2 = HttpHeaders()
        headers2.setBearerAuth(expertToken)
        val response2: ResponseEntity<ProblemDetail> =
            restTemplate.exchange("$prefixEndPoint/$ticketId/chat-messages", HttpMethod.GET, HttpEntity(null, headers2))
        assert(response2.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `get messages for a ticket as a Manager`() {
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val headers1 = HttpHeaders()
        headers1.setBearerAuth(customerToken)
        val request1 = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
            })
        val response1 = restTemplate.exchange<Void>(request1)
        assert(response1.statusCode == HttpStatus.CREATED)
        assert(response1.headers.location.toString().isNotBlank())
        val headers2 = HttpHeaders()
        headers2.setBearerAuth(managerToken)
        val response2: ResponseEntity<Set<ChatMessageOutDTO>> =
            restTemplate.exchange("$prefixEndPoint/$ticketId/chat-messages", HttpMethod.GET, HttpEntity(null, headers2))
        assert(response2.statusCode == HttpStatus.OK)
        assert(response2.body!!.size == 1)
        assert(response2.body!!.elementAt(0).authorEmail == customer.email)
        assert(response2.body!!.elementAt(0).body == messageBody)
    }

    @Test
    fun `get all messages for a non existent ticket`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val responseGet: ResponseEntity<ProblemDetail> =
            restTemplate.exchange("$prefixEndPoint/1/chat-messages", HttpMethod.GET, HttpEntity(null, headers))
        assert(responseGet.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `get a non existent messages`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val responseGet: ResponseEntity<ProblemDetail> =
            restTemplate.exchange("$prefixEndPoint/$ticketId/chat-messages/1", HttpMethod.GET, HttpEntity(null, headers))
        assert(responseGet.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `get a non existent attachment`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet: ResponseEntity<ProblemDetail> =
            restTemplate.exchange("${response.headers.location}/attachments/1", HttpMethod.GET, HttpEntity(null, headers))
        assert(responseGet.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `create a simple message for a ticket as a Customer`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers),  ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
    }

    @Test
    fun `create a simple message for a ticket as an Expert`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(expertToken)
        val ticketId = insertTicket()
        assignTicket(ticketId, expert, manager)
        val messageBody = "This is a test message"
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers),  ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == expert.email)
        assert(responseGet.body!!.body == messageBody)
    }

    @Test
    fun `create a message for a ticket with attachments`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val file1Content = "test".toByteArray()
        val file2Content = "test2".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
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
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers), ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 2)
    }

    @Test
    fun `create a message for a ticket with attachments and get the attachments`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val fileName = "test.txt"
        val fileContent = "test".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO(messageBody))
                add("files", object : ByteArrayResource(fileContent) {
                    override fun getFilename(): String = fileName
                })
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers), ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 1)
        val responseGetAttachment = restTemplate.exchange(
            responseGet.body!!.stubAttachments!!.elementAt(0).url,
            HttpMethod.GET,
            HttpEntity(null, headers),
            ByteArrayResource::class.java
        )
        assert(responseGetAttachment.statusCode == HttpStatus.OK)
        assert(responseGetAttachment.headers.contentType == MediaType.TEXT_PLAIN)
        assert(responseGetAttachment.headers.contentDisposition.isAttachment)
        assert(responseGetAttachment.headers.contentDisposition.filename == fileName)
        assert(responseGetAttachment.body!!.byteArray.contentEquals(fileContent))
    }

    @Test
    fun `create a message for a ticket with attachments and get the attachments as an Expert`() {
        val headers1 = HttpHeaders()
        headers1.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        assignTicket(ticketId, expert, manager)
        val messageBody = "This is a test message"
        val fileName = "test.txt"
        val fileContent = "test".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
                add("files", object : ByteArrayResource(fileContent) {
                    override fun getFilename(): String = fileName
                })
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val headers2 = HttpHeaders()
        headers2.setBearerAuth(expertToken)
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers2), ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 1)
        val responseGetAttachment = restTemplate.exchange(
            responseGet.body!!.stubAttachments!!.elementAt(0).url,
            HttpMethod.GET,
            HttpEntity(null, headers2),
            ByteArrayResource::class.java
        )
        assert(responseGetAttachment.statusCode == HttpStatus.OK)
        assert(responseGetAttachment.headers.contentType == MediaType.TEXT_PLAIN)
        assert(responseGetAttachment.headers.contentDisposition.isAttachment)
        assert(responseGetAttachment.headers.contentDisposition.filename == fileName)
        assert(responseGetAttachment.body!!.byteArray.contentEquals(fileContent))
    }

    @Test
    fun `create a message for a non assigned ticket with attachments and get the attachments as an Expert`() {
        val headers1 = HttpHeaders()
        headers1.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val fileName = "test.txt"
        val fileContent = "test".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
                add("files", object : ByteArrayResource(fileContent) {
                    override fun getFilename(): String = fileName
                })
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val headers2 = HttpHeaders()
        headers2.setBearerAuth(expertToken)
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers1), ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 1)
        val responseGetAttachment = restTemplate.exchange(
            responseGet.body!!.stubAttachments!!.elementAt(0).url,
            HttpMethod.GET,
            HttpEntity(null, headers2),
            ProblemDetail::class.java
        )
        assert(responseGetAttachment.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `create a message for a ticket with attachments and get the attachments as a Manager`() {
        val headers1 = HttpHeaders()
        headers1.setBearerAuth(customerToken)
        val ticketId = insertTicket()
        val messageBody = "This is a test message"
        val fileName = "test.txt"
        val fileContent = "test".toByteArray()
        val request = RequestEntity.post("$prefixEndPoint/$ticketId/chat-messages")
            .headers(headers1)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", ChatMessageInDTO( messageBody))
                add("files", object : ByteArrayResource(fileContent) {
                    override fun getFilename(): String = fileName
                })
            })
        val response = restTemplate.exchange<Void>(request)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val headers2 = HttpHeaders()
        headers2.setBearerAuth(managerToken)
        val responseGet = restTemplate.exchange(response.headers.location, HttpMethod.GET, HttpEntity(null, headers2), ChatMessageOutDTO::class.java)
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.authorEmail == customer.email)
        assert(responseGet.body!!.body == messageBody)
        assert(responseGet.body!!.stubAttachments!!.size == 1)
        val responseGetAttachment = restTemplate.exchange(
            responseGet.body!!.stubAttachments!!.elementAt(0).url,
            HttpMethod.GET,
            HttpEntity(null, headers2),
            ByteArrayResource::class.java
        )
        assert(responseGetAttachment.statusCode == HttpStatus.OK)
        assert(responseGetAttachment.headers.contentType == MediaType.TEXT_PLAIN)
        assert(responseGetAttachment.headers.contentDisposition.isAttachment)
        assert(responseGetAttachment.headers.contentDisposition.filename == fileName)
        assert(responseGetAttachment.body!!.byteArray.contentEquals(fileContent))
    }
}*/