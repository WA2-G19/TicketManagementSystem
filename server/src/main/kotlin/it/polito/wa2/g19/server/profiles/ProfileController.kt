package it.polito.wa2.g19.server.profiles

import it.polito.wa2.g19.server.ticketing.chat.ChatMessageInDTO
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.security.Principal

@RestController
@RequestMapping("/API")
class ProfileController {

    @GetMapping("/customers/login")
    fun loginCustomer(
        @RequestBody(required = true)
        username: String,
        @RequestBody(required = true)
        password: String): String {
        val restTemplate = RestTemplate()
//        val request = RequestEntity.post("http://localhost:8081/realms/ticket_management_system/protocol/openid-connect/auth?response_type=code&client_id=TicketManagementSystem")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .body(LinkedMultiValueMap<String, Any>().apply {
//                add("message", ChatMessageInDTO(customer.email, messageBody))
//            })
        return ""
    }
}