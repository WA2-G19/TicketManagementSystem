package it.polito.wa2.g19.server.testingauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
class TestingAuthController {

    @GetMapping("/public")
    fun publicApi(): Map<String, Any> {
        val auth = SecurityContextHolder.getContext().authentication
        val jwtObj = auth.principal as Jwt
        return jwtObj.claims
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/name")
    fun nameApi(): String {
        val auth = SecurityContextHolder.getContext().authentication
        val jwtObj = auth.principal as Jwt
        return jwtObj.claims["email"]!!.toString()
    }

    @PreAuthorize("isAuthenticated() and #email == #token.tokenAttributes['email']")
    @GetMapping("/personal/{email}")
    fun personalApi(
        @Valid
        @Email
        @PathVariable(required = true)
        email: String,
        token: AbstractOAuth2TokenAuthenticationToken<*>
    ): String {
        return "Ok"
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
        @RequestBody user: Map<String, String>,
    ): Test {
        val url = "http://localhost:8081/realms/ticket_management_system/protocol/openid-connect/token"
        val restTemplate = RestTemplate()

        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("username", user["username"])
        map.add("password", user["password"])
        map.add("client_id", "TicketManagementSystem")
        map.add("client_secret", "eoM7Xo7Ft93eyph81RnfSiNcJ9Cawvfw")
        map.add("grant_type", "password")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val request = HttpEntity(map, headers)
        val response = restTemplate.postForObject(
            url,
            request,
            String::class.java
        )

        return ObjectMapper().readValue(response, Test::class.java)

    }

}

data class Test (
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("refresh_expires_in") val refreshExpires: Int,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("refresh_token") val refreshToken: String,
    @JsonProperty("not-before-policy") val notBeforePolicy: Int,
    @JsonProperty("session_state") val sessionState: String,
    @JsonProperty("scope") val scope: String,
    @JsonProperty("token_type") val tokenType: String
)