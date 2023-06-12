package it.polito.wa2.g19.server.integration.warranty

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
import it.polito.wa2.g19.server.profiles.vendors.Vendor
import it.polito.wa2.g19.server.profiles.vendors.VendorRepository
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusRepository
import it.polito.wa2.g19.server.ticketing.tickets.PriorityLevelRepository
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g19.server.warranty.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.keycloak.admin.client.Keycloak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WarrantyTest {


    @Autowired
    private lateinit var keycloak: Keycloak

    @Value("\${keycloak.admin.realm}")
    private lateinit var realmName: String

    private val prefixEndPoint = "/API/warranty"

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
            registry.add("keycloakBaseUrl", { keycloakBaseUrl })
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                { "${keycloakBaseUrl}/realms/ticket_management_system" })
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                { "${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs" })
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
    private lateinit var vendorToken: String

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
    fun populateDatabase() {
        keycloak.realm(realmName).users().list().forEach {
            println(it.email)

        }


        println("----populating database------")
        Util.mockCustomers().forEach {
            if (::customer.isInitialized)
                otherCustomer = customer
            customer = customerRepository.save(it)
        }

        customer = customerRepository.save(Util.mockMainCustomer())

        Util.mockManagers().forEach {
            it.id = UUID.randomUUID()
            manager = staffRepository.save(it)
        }
        manager = staffRepository.save(Util.mockMainManager())

        Util.mockExperts().forEach {
            if (::expert.isInitialized)
                otherExpert = expert
            expert = staffRepository.save(it)
        }
        expert = staffRepository.save(Util.mockMainExpert())
        Util.mockPriorityLevels().forEach {
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
    fun destroyDatabase() {
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
    fun refreshCustomerToken() {
        val loginDTO = LoginDTO(customer.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST)
        customerToken = response.body!!
    }

    @BeforeEach
    fun refreshExpertToken() {
        val loginDTO = LoginDTO(expert.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST)
        expertToken = response.body!!
    }

    @BeforeEach
    fun refreshVendorToken() {
        keycloak
        val loginDTO = LoginDTO(vendor.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST)
        vendorToken = response.body!!
    }

    @BeforeEach
    fun refreshManagerToken() {
        val loginDTO = LoginDTO(manager.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST)
        managerToken = response.body!!
    }


    @Test
    fun `create a new warranty is successful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(vendorToken)
        val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(1))
        val response = restTemplate.exchange(
            "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
            WarrantyOutDTO::class.java
        )
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body!!.vendorEmail == vendor.email)
        assert(response.body!!.customerEmail == null)
        assert(response.body!!.productEan == product.ean)

        headers.setBearerAuth(managerToken)
        val getResponse = restTemplate.exchange(
            response.headers.location,
            HttpMethod.GET,
            HttpEntity(null, headers),
            WarrantyOutDTO::class.java
        )
        println(getResponse.body!!.vendorEmail)
        assert(getResponse.body!!.vendorEmail == vendor.email)
        assert(getResponse.body!!.customerEmail == null)
        assert(getResponse.body!!.productEan == product.ean)
    }


    @Test
    fun `not vendor cannot create a warranty`() {
        for (token in listOf(customerToken, managerToken, expertToken)) {
            val headers = HttpHeaders()
            headers.setBearerAuth(token)
            val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(1))
            val response = restTemplate.exchange(
                "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
                WarrantyOutDTO::class.java
            )

            assert(response.statusCode == HttpStatus.FORBIDDEN)
        }

        val headers = HttpHeaders()

        val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(1))
        val response = restTemplate.exchange(
            "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
            WarrantyOutDTO::class.java
        )

        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `activate a existing warranty is successful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(vendorToken)
        val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(1))
        val response = restTemplate.exchange(
            "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
            WarrantyOutDTO::class.java
        )
        assert(response.statusCode == HttpStatus.CREATED)
        val warrantyId = response.body!!.id

        headers.setBearerAuth(customerToken)
        val responseActivate = restTemplate.exchange(
            "$prefixEndPoint/$warrantyId/activate", HttpMethod.POST, HttpEntity(null, headers),
            WarrantyOutDTO::class.java
        )
        assert(responseActivate.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `not customer cannot activate an existing warranty`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(vendorToken)
        val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(1))
        val response = restTemplate.exchange(
            "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
            WarrantyOutDTO::class.java
        )
        assert(response.statusCode == HttpStatus.CREATED)
        val warrantyId = response.body!!.id
        for (token in listOf(expertToken, managerToken, vendorToken)) {
            headers.setBearerAuth(token)
            val responseActivate = restTemplate.exchange(
                "$prefixEndPoint/$warrantyId/activate", HttpMethod.POST, HttpEntity(null, headers),
                ProblemDetail::class.java
            )
            assert(responseActivate.statusCode == HttpStatus.FORBIDDEN)
        }

        val emptyHeaders = HttpHeaders()
        val responseActivate = restTemplate.exchange(
            "$prefixEndPoint/$warrantyId/activate", HttpMethod.POST, HttpEntity(null, emptyHeaders),
            ProblemDetail::class.java
        )
        assert(responseActivate.statusCode == HttpStatus.UNAUTHORIZED)

    }

    @Test
    fun `activate a not existing warranty is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val responseActivate = restTemplate.exchange(
            "$prefixEndPoint/${UUID.randomUUID()}/activate",
            HttpMethod.POST,
            HttpEntity(null, headers),
            ProblemDetail::class.java
        )
        assert(responseActivate.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `activate an activated warranty is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(vendorToken)
        val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(1))
        val response = restTemplate.exchange(
            "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
            WarrantyOutDTO::class.java
        )
        assert(response.statusCode == HttpStatus.CREATED)
        val warrantyId = response.body!!.id

        headers.setBearerAuth(customerToken)
        val responseActivate = restTemplate.exchange(
            "$prefixEndPoint/$warrantyId/activate", HttpMethod.POST, HttpEntity(null, headers),
            WarrantyOutDTO::class.java
        )
        val responseReactivate = restTemplate.exchange(
            "$prefixEndPoint/$warrantyId/activate",
            HttpMethod.POST,
            HttpEntity(null, headers),
            ProblemDetail::class.java
        )
        assert(responseReactivate.statusCode == HttpStatus.CONFLICT)
        assert(responseReactivate.body!!.detail == WarrantyAlreadyActivated().message)
    }


    @Test
    fun `activate an expired warranty is unsuccessful`() {
        val headers = HttpHeaders()
        headers.setBearerAuth(vendorToken)
        val warrantyInDTO = WarrantyInDTO(product.ean, Duration.ofDays(-1))
        val response = restTemplate.exchange(
            "$prefixEndPoint", HttpMethod.POST, HttpEntity(warrantyInDTO, headers),
            WarrantyOutDTO::class.java
        )
        assert(response.statusCode == HttpStatus.CREATED)
        val warrantyId = response.body!!.id

        headers.setBearerAuth(customerToken)
        val responseActivate = restTemplate.exchange(
            "$prefixEndPoint/$warrantyId/activate", HttpMethod.POST, HttpEntity(null, headers),
            ProblemDetail::class.java
        )
        assert(responseActivate.body!!.detail == WarrantyExpiredException().message)
    }
}
