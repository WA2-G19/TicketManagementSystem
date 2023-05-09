package it.polito.wa2.g19.server.testingauth

import com.nimbusds.jose.proc.SecurityContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.security.Principal

@RestController
class TestingAuthController {

    @GetMapping("/public")
    fun publicApi(): Map<String, Any> {
        val auth = SecurityContextHolder.getContext().authentication
        val jwtObj = auth.principal as Jwt
        return jwtObj.claims
    }


    @PreAuthorize("hasRole('Client')")
    @GetMapping("/client")
    fun clientApi(): String {
        return "You are in client view"
    }


    @PreAuthorize("hasRole('Expert')")
    @GetMapping("/expert")
    fun expertApi(): String {
        return "You are in expert view"
    }


    @PreAuthorize("hasRole('Manager')")
    @GetMapping("/manager")
    fun managerApi(): String {
        return "You are in manager view"
    }

    @PostMapping("/auth/login")
    fun login(
        @RequestBody userName: String,
        @RequestBody password: String
    ): String {

        val url = "http://localhost:8081/realms/ticket_management_system/protocol/openid-connect/token"
        val restTemplate = RestTemplate()
        val map = LinkedMultiValueMap<String, String>()
        map.add("username", userName)
        map.add("username", password)

        map.add("client_id", "TicketManagementSystem")
        map.add("client_secret", "eoM7Xo7Ft93eyph81RnfSiNcJ9Cawvfw")
        map.add("grant_type", "password")

        val request = RequestEntity
            .post(url)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(map)

        val response = restTemplate.exchange<Any>(request)
        return response.toString()

    }

}