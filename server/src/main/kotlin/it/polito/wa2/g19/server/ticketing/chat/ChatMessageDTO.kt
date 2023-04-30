package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.AttachmentProjection
import it.polito.wa2.g19.server.ticketing.attachments.StubAttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toStubDTO
import java.io.File
import java.time.LocalDateTime

abstract class ChatMessageDTO(
    var authorEmail: String,
    var body: String,
)

class ChatMessageInDTO(authorEmail: String, body: String, val attachments: Set<File>?):
    ChatMessageDTO(authorEmail, body)

class ChatMessageOutDTO( authorEmail: String, body: String, val stubAttachments: Set<StubAttachmentDTO>?,
                         val timestamp: LocalDateTime):
        ChatMessageDTO(authorEmail, body)



fun ChatMessage.toOutDTO(attachmentsProjection: List<AttachmentProjection>) = ChatMessageOutDTO(
    author.email,
    body,
    attachmentsProjection.map { it.toStubDTO()!! }.toSet(),
    timestamp
)