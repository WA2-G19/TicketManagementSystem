package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.AttachmentProjection
import it.polito.wa2.g19.server.ticketing.attachments.StubAttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toStubDTO
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

abstract class ChatMessageDTO(
    @field:Email(message = "authorEmail must be a valid email")
    @field:NotBlank(message = "authorEmail must be not blank")
    var authorEmail: String,
    @field:NotBlank(message = "message body must be not blank")
    var body: String,
)

class ChatMessageInDTO(authorEmail: String, body: String):
    ChatMessageDTO(authorEmail, body)

class ChatMessageOutDTO(val id: Int, authorEmail: String, body: String, val stubAttachments: Set<StubAttachmentDTO>?,
                         val timestamp: LocalDateTime):
        ChatMessageDTO(authorEmail, body)



fun ChatMessage.toOutDTO(attachmentsProjection: List<AttachmentProjection>) = ChatMessageOutDTO(
    getId()!!,
    getAuthor().email,
    body,
    attachmentsProjection.map { it.toStubDTO() }.toSet(),
    timestamp
)