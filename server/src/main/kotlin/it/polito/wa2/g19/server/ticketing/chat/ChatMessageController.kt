package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.Util
import jakarta.validation.Valid
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
    private val handlerMapping: RequestMappingHandlerMapping
) {


    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket
    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}")
    @ResponseStatus(HttpStatus.OK)
    fun getMessage(@PathVariable ticketId: Int,
                   @PathVariable chatMessageId: Int): ChatMessageOutDTO {
        return chatMessageService.getChatMessage(ticketId,chatMessageId).apply {
            stubAttachments?.forEach {stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId,chatMessageId,stub.url)
            }
        }
    }

    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket (InProgress or Closed)
    // - Client can see ONLY its ticket
    @GetMapping("/{ticketId}/chat-messages")
    @ResponseStatus(HttpStatus.OK)
    fun getMessages(
        @PathVariable ticketId: Int
    ): Set<ChatMessageOutDTO> {
        val messages =  chatMessageService.getChatMessages(ticketId)
        messages.forEach {
            it.stubAttachments?.forEach {stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId, it.id,stub.url)
            }
        }
        return messages
    }

    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket
    @PostMapping("/{ticketId}/chat-messages",
        consumes =  [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun postChatMessage(
        @PathVariable ticketId: Int,
        @RequestPart message: ChatMessageInDTO,
        @RequestPart(required = false) files: List<MultipartFile>?,
    ): ResponseEntity<Void> {

        val id = chatMessageService.insertChatMessage(ticketId, message, files)
        val headers = HttpHeaders()
        headers.location = URI.create( Util.getUri(handlerMapping, ::getMessage.name,ticketId,id) )
        return ResponseEntity(null, headers, HttpStatus.CREATED)
    }

    // User is authenticated
    //  - Manager can do everything
    // - Expert should be assigned to ticket
    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    fun getAttachment(
        @Valid
        @PathVariable(required = true) ticketId: Int,
        @PathVariable(required = true) chatMessageId: ChatMessage,
        @PathVariable(required = true) attachmentId: Int,
    ): ResponseEntity<ByteArrayResource>{
        val attachmentDTO = chatMessageService.getAttachment(ticketId, attachmentId)
        val headers = HttpHeaders()
        headers.set("content-disposition", "attachment; filename=${attachmentDTO.name}")
        headers.contentType = MediaType.parseMediaType(attachmentDTO.contentType)
        headers["TMS-Creation-Time"] = attachmentDTO.timestamp.toString()
        headers["TMS-Length"] = attachmentDTO.length.toString()
        return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
    }
}