package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.Util
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI


@RestController
@Validated
@RequestMapping("/API/tickets")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val handlerMapping: RequestMappingHandlerMapping
) {


    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}")
    @ResponseStatus(HttpStatus.OK)
    fun getMessage(@PathVariable ticketId: String,
                   @PathVariable chatMessageId: Int): ChatMessageOutDTO {
        return chatMessageService.getChatMessage(chatMessageId).let {
            it.stubAttachments?.forEach {stub ->
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId,chatMessageId,stub.url)
            }
            it
        }
    }


    @GetMapping("/{ticketId}/chat-messages")
    @ResponseStatus(HttpStatus.OK)
    fun getMessages(
        @PathVariable ticketId: Int
    ): Set<ChatMessageOutDTO> {
        val messages =  chatMessageService.getChatMessages()
        messages.forEach {
            it.stubAttachments?.forEach {stub ->
                println("oooooooooooooo")
                stub.url = Util.getUri(handlerMapping, ::getAttachment.name, ticketId, stub.url)
            }
        }
        return messages
    }

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
    @GetMapping("/{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId}", produces =  [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun getAttachment(
        @Valid
        @PathVariable(required = true) ticketId: Int,
        @PathVariable(required = true) chatMessageId: ChatMessage,
        @PathVariable(required = true) attachmentId: Int,
    ): ResponseEntity<ByteArrayResource>{
        val attachmentDTO = chatMessageService.getAttachment(ticketId, attachmentId)
        val headers = HttpHeaders()
        headers.setContentDispositionFormData("attachment", attachmentDTO.name)
        headers.contentType = MediaType.parseMediaType(attachmentDTO.contentType)
        headers["TMS-Creation-Time"] = attachmentDTO.timestamp.toString()
        headers["TMS-Lenght"] = attachmentDTO.length.toString()
        return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
    }


}