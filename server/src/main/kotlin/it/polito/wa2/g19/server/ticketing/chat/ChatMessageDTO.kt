package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.AttachmentProjection
import it.polito.wa2.g19.server.ticketing.attachments.StubAttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toStubDTO
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

abstract class ChatMessageDTO(
    @field:NotBlank(message = "message body must be not blank")
    var body: String,
)

class ChatMessageInDTO( body: String):
    ChatMessageDTO(body)

class ChatMessageOutDTO(val id: Int,
                        var authorEmail:  String,
                        body: String,
                        val stubAttachments: Set<StubAttachmentDTO>?,
                        val timestamp: LocalDateTime):
        ChatMessageDTO(body)



fun ChatMessage.toOutDTO(attachmentsProjection: List<AttachmentProjection>, authorEmail: String) = ChatMessageOutDTO(
    id!!.toInt(),
    authorEmail,
    body,
    attachmentsProjection.map { it.toStubDTO() }.toSet(),
    timestamp
)