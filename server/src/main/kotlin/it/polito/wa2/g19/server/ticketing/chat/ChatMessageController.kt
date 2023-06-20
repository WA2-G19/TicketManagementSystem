package it.polito.wa2.g19.server.ticketing.chat

import io.micrometer.observation.annotation.Observed
import it.polito.wa2.g19.server.common.Role
import it.polito.wa2.g19.server.common.Util
import it.polito.wa2.g19.server.ticketing.tickets.ForbiddenException
import it.polito.wa2.g19.server.ticketing.tickets.TicketService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.URI


@RestController
@Validated
@CrossOrigin
@RequestMapping("/API/tickets")
@Observed
class ChatMessageController(
    private val ticketService: TicketService,
    private val chatMessageService: ChatMessageService,
    @Autowired
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: RequestMappingHandlerMapping,
) {

    @GetMapping("/chat-messages/unread")
    @ResponseStatus(HttpStatus.OK)
    fun getAllUnreadMessages(): Map<Int, Int> {
        val principal = SecurityContextHolder.getContext().authentication
        val email = principal.name

        val tickets = when (Role.valueOf(principal.authorities.stream().findFirst().get().authority)) {
            /*If the customer param is specified ignores it*/
            Role.ROLE_Client -> ticketService.getTickets(email)
            /*If the expert param is specified ignores it*/
            Role.ROLE_Expert -> ticketService.getTickets(null, email,)

            Role.ROLE_Manager -> ticketService.getTickets()

            Role.ROLE_Vendor -> throw ForbiddenException()
        }

        return tickets.associate {
            it.id!! to chatMessageService.getUnreadMessages(it.id)
        }
    }

    @GetMapping("/{ticketId}/chat-messages/unread")
    @ResponseStatus(HttpStatus.OK)
    fun getUnreadMessagesForTicket(@PathVariable ticketId: Int): Int {
        return chatMessageService.getUnreadMessages(ticketId)
    }

    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}")
    @ResponseStatus(HttpStatus.OK)
    fun getMessage(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int,
        @PathVariable chatMessageId: Int
    ): ChatMessageOutDTO {
        return chatMessageService.getChatMessage(ticketId, chatMessageId).apply {
            stubAttachments?.forEach { stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId, chatMessageId, stub.url)
            }
        }
    }

    @GetMapping("/{ticketId}/chat-messages")
    @ResponseStatus(HttpStatus.OK)
    fun getMessages(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int
    ): Set<ChatMessageOutDTO> {
        val messages = chatMessageService.getChatMessages(ticketId)
        messages.forEach {
            it.stubAttachments?.forEach { stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId, it.id, stub.url)
            }
        }
        return messages
    }

    @PostMapping(
        "/{ticketId}/chat-messages",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun postChatMessage(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int,
        @RequestPart message: ChatMessageInDTO,
        @RequestPart(required = false) files: List<MultipartFile>?,
    ): ResponseEntity<Void> {
        val id = chatMessageService.insertChatMessage(ticketId, message, files)
        val headers = HttpHeaders()
        headers.location = URI.create(Util.getUri(handlerMapping, ::getMessage.name, ticketId, id))
        return ResponseEntity(null, headers, HttpStatus.CREATED)
    }

    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    fun getAttachment(
        principal: JwtAuthenticationToken,
        @Valid
        @PathVariable(required = true) ticketId: Int,
        @PathVariable(required = true) chatMessageId: ChatMessage,
        @PathVariable(required = true) attachmentId: Int,
    ): ResponseEntity<ByteArrayResource> {
        val attachmentDTO = chatMessageService.getAttachment(ticketId, attachmentId)
        val headers = HttpHeaders()
        headers.set("content-disposition", "attachment; filename=${attachmentDTO.name}")
        headers.contentType = MediaType.parseMediaType(attachmentDTO.contentType)
        headers["TMS-Creation-Time"] = attachmentDTO.timestamp.toString()
        headers["TMS-Length"] = attachmentDTO.length.toString()
        return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
    }
}