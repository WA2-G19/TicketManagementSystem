package it.polito.wa2.g19.server.ticketing

import java.time.LocalDateTime

data class ChatMessageDTO(
    var ticketId: Int,
    var authorId: Int,
    var attachmentsIds: Set<Int>,
    var body: String,
    var timestamp: LocalDateTime
)

fun ChatMessage.toDTO() = ChatMessageDTO(
    ticket.getId()!!,
    author.getId()!!,
    attachments.map { it.getId()!! }.toSet(),
    body,
    timestamp
)