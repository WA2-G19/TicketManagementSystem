package it.polito.wa2.g19.server.ticketing

import java.time.LocalDateTime

data class AttachmentDTO(
    var chatMessageId: Int,
    var contentType: String,
    var length: Int,
    var content: ByteArray,
    var timestamp: LocalDateTime
)

fun Attachment.toDTO(): AttachmentDTO = AttachmentDTO(
    message.getId()!!,
    contentType,
    length,
    content,
    timestamp
)