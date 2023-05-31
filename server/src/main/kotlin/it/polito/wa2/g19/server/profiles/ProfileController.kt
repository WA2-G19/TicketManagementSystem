package it.polito.wa2.g19.server.profiles

import io.micrometer.observation.annotation.Observed
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/API")
@Observed
@Slf4j
class ProfileController {

    @Value("\${keycloakBaseUrl}")
    private lateinit var keycloakBaseUrl: String

    private val log: Logger = LoggerFactory.getLogger(ProfileController::class.java)

    @PostMapping("/login")
    fun loginCustomer(
        @RequestBody(required = true)
        login: LoginDTO): String {
        log.info("User {} tried to log in.", login.username)
        val restTemplate = RestTemplate()
        val request = RequestEntity.post("${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply {
                add("username", login.username)
                add("password", login.password)
                add("client_id", "TicketManagementSystem")
                add("grant_type", "password")
                add("client_secret", "eoM7Xo7Ft93eyph81RnfSiNcJ9Cawvfw")
            })
        val response = restTemplate.exchange(request, object: ParameterizedTypeReference<LinkedHashMap<String, Any>>() {})
        return response.body!!["access_token"].toString()
    }


}