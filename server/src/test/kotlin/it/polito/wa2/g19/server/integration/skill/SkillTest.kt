package it.polito.wa2.g19.server.integration.skill

import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.wa2.g19.server.Util
import it.polito.wa2.g19.server.integration.ticketing.TicketTest
import it.polito.wa2.g19.server.profiles.LoginDTO
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.skills.SkillDTO
import it.polito.wa2.g19.server.skills.SkillRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class SkillTest {

    private val prefixEndPoint = "/API/skill"

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
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri") { "${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs" }
        }
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var staffRepository: StaffRepository
    @Autowired
    lateinit var skillRepository: SkillRepository

    private lateinit var managerToken: String

    @BeforeEach
    fun populateDatabase() {
        if(!TicketTest.keycloak.isRunning){
            TicketTest.keycloak.start()
        }
        println("----populating database------")
        staffRepository.save(Util.mockMainManager())
        println("---------------------------------")
    }

    @AfterEach
    fun destroyDatabase() {
        staffRepository.deleteAll()
        skillRepository.deleteAll()
    }

    @BeforeEach
    fun refreshManagerToken(){
        val loginDTO = LoginDTO(Util.mockMainManager().email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        managerToken = response.body!!
    }

    fun managerHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.setBearerAuth(managerToken)
        return headers
    }

    @Test
    fun `insert skill`() {
        val skill = SkillDTO("Test")
        val response = restTemplate.postForEntity<SkillDTO>(prefixEndPoint, HttpEntity(skill, managerHeaders()), HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `insert skill twice`() {
        val skill = SkillDTO("Test")
        val response = restTemplate.postForEntity<SkillDTO>(prefixEndPoint, HttpEntity(skill, managerHeaders()), HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)
        val response2 = restTemplate.postForEntity<ProblemDetail>(prefixEndPoint, HttpEntity(skill, managerHeaders()), HttpMethod.POST)
        assert(response2.statusCode == HttpStatus.CONFLICT)
    }

    @Test
    fun `get all skills`() {
        val response: ResponseEntity<List<SkillDTO>> = restTemplate.exchange(prefixEndPoint, HttpMethod.GET, HttpEntity(null, managerHeaders()))
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body!!.isEmpty())
    }

    @Test
    fun `insert skill and delete it`() {
        val skill = SkillDTO("Test")
        val response = restTemplate.postForEntity<SkillDTO>(prefixEndPoint, HttpEntity(skill, managerHeaders()), HttpMethod.POST)
        assert(response.statusCode == HttpStatus.CREATED)
        val response2: ResponseEntity<SkillDTO> = restTemplate.exchange(prefixEndPoint, HttpMethod.DELETE, HttpEntity(skill, managerHeaders()))
        assert(response2.statusCode == HttpStatus.OK)
    }

    @Test
    fun `delete non existent skill`() {
        val skill = SkillDTO("Test")
        val response: ResponseEntity<ProblemDetail> = restTemplate.exchange(prefixEndPoint, HttpMethod.DELETE, HttpEntity(skill, managerHeaders()))
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }
}