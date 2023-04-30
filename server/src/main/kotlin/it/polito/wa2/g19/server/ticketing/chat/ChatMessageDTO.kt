package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.AttachmentProjection
import it.polito.wa2.g19.server.ticketing.attachments.StubAttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toStubDTO
import jakarta.persistence.Id
import java.io.File
import java.time.LocalDateTime

abstract class ChatMessageDTO(

    var authorEmail: String,
    var body: String,
)

class ChatMessageInDTO(authorEmail: String, body: String):
    ChatMessageDTO(authorEmail, body)

class ChatMessageOutDTO(val id: Int, authorEmail: String, body: String, val stubAttachments: Set<StubAttachmentDTO>?,
                         val timestamp: LocalDateTime):
        ChatMessageDTO(authorEmail, body)



fun ChatMessage.toOutDTO(attachmentsProjection: List<AttachmentProjection>) = ChatMessageOutDTO(
    getId()!!,
    author.email,
    body,
    attachmentsProjection.map { it.toStubDTO()!! }.toSet(),
    timestamp
)