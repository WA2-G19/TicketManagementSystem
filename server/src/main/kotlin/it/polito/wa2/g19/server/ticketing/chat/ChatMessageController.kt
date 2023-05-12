package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.Role
import it.polito.wa2.g19.server.common.Util
import it.polito.wa2.g19.server.ticketing.statuses.TicketStatusEnum
import it.polito.wa2.g19.server.ticketing.tickets.TicketService
import jakarta.validation.Valid
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.net.URI


@RestController
@Validated
@RequestMapping("/API/tickets")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val handlerMapping: RequestMappingHandlerMapping,
    private val ticketService: TicketService,
) {


    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket
    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    fun getMessage(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int,
        @PathVariable chatMessageId: Int
    ): ChatMessageOutDTO {
        val role = Role.valueOf(principal.authorities.stream().findFirst().get().authority)
        val email = principal.name
        val chatMessage = chatMessageService.getChatMessage(ticketId, chatMessageId).apply {
            stubAttachments?.forEach { stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId, chatMessageId, stub.url)
            }
        }
        when (role) {
            Role.ROLE_Client -> {
                val ticket = ticketService.getTicket(ticketId)
                if (ticket.customerEmail == email) {
                    return chatMessage
                }
            }

            Role.ROLE_Expert -> {
                val status = ticketService.getFinalStatus(ticketId)
                if (status.ticket.expert?.email == email) {
                    return chatMessage
                }
            }

            Role.ROLE_Manager -> {
                return chatMessage
            }
        }
        throw NotAllowedToThisMethodException()
    }

    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket (InProgress or Closed)
    // - Client can see ONLY its ticket
    @GetMapping("/{ticketId}/chat-messages")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    fun getMessages(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int
    ): Set<ChatMessageOutDTO> {
        val role = Role.valueOf(principal.authorities.stream().findFirst().get().authority)
        val email = principal.name
        val messages = chatMessageService.getChatMessages(ticketId)
        messages.forEach {
            it.stubAttachments?.forEach { stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId, it.id, stub.url)
            }
        }
        when (role) {
            Role.ROLE_Client -> {
                val ticket = ticketService.getTicket(ticketId)
                if (ticket.customerEmail == email) {
                    return messages
                }
            }

            Role.ROLE_Expert -> {
                val status = ticketService.getFinalStatus(ticketId)
                if (status.ticket.expert?.email == email && (status.ticket.status == TicketStatusEnum.Closed || status.ticket.status == TicketStatusEnum.InProgress)) {
                    return messages
                }
            }

            Role.ROLE_Manager -> {
                return messages
            }
        }
        throw NotAllowedToThisMethodException()
    }

    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket
    @PostMapping(
        "/{ticketId}/chat-messages",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun postChatMessage(
        principal: JwtAuthenticationToken,
        @PathVariable ticketId: Int,
        @RequestPart message: ChatMessageInDTO,
        @RequestPart(required = false) files: List<MultipartFile>?,
    ): ResponseEntity<Void> {
        val role = Role.valueOf(principal.authorities.stream().findFirst().get().authority)
        val email = principal.name
        val id = chatMessageService.insertChatMessage(ticketId, message, files)
        val headers = HttpHeaders()
        headers.location = URI.create(Util.getUri(handlerMapping, ::getMessage.name, ticketId, id))
        when (role) {
            Role.ROLE_Client -> {
                val ticket = ticketService.getTicket(ticketId)
                if (ticket.customerEmail == email) {
                    return ResponseEntity(null, headers, HttpStatus.CREATED)
                }
            }

            Role.ROLE_Expert -> {
                val status = ticketService.getFinalStatus(ticketId)
                if (status.ticket.expert?.email == email) {
                    return ResponseEntity(null, headers, HttpStatus.CREATED)
                }
            }

            Role.ROLE_Manager -> {
                return ResponseEntity(null, headers, HttpStatus.CREATED)
            }
        }
        throw NotAllowedToThisMethodException()
    }

    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket
    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    fun getAttachment(
        principal: JwtAuthenticationToken,
        @Valid
        @PathVariable(required = true) ticketId: Int,
        @PathVariable(required = true) chatMessageId: ChatMessage,
        @PathVariable(required = true) attachmentId: Int,
    ): ResponseEntity<ByteArrayResource> {
        val role = Role.valueOf(principal.authorities.stream().findFirst().get().authority)
        val email = principal.name
        val attachmentDTO = chatMessageService.getAttachment(ticketId, attachmentId)
        val headers = HttpHeaders()
        headers.set("content-disposition", "attachment; filename=${attachmentDTO.name}")
        headers.contentType = MediaType.parseMediaType(attachmentDTO.contentType)
        headers["TMS-Creation-Time"] = attachmentDTO.timestamp.toString()
        headers["TMS-Length"] = attachmentDTO.length.toString()
        when (role) {
            Role.ROLE_Client -> {
                val ticket = ticketService.getTicket(ticketId)
                if (ticket.customerEmail == email) {
                    return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
                }
            }

            Role.ROLE_Expert -> {
                val status = ticketService.getFinalStatus(ticketId)
                if (status.ticket.expert?.email == email) {
                    return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
                }
            }

            Role.ROLE_Manager -> {
                return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
            }
        }
        throw NotAllowedToThisMethodException()
    }
}