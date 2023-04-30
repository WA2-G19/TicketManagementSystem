package it.polito.wa2.g19.server.ticketing.attachments

import org.springframework.core.io.ByteArrayResource
import java.time.LocalDateTime

data class AttachmentDTO(
    var name: String,
    var contentType: String,
    var length: Int,
    var content: ByteArrayResource,
    var timestamp: LocalDateTime
)


class StubAttachmentDTO(
    var name: String,
    var contentType: String,
    var length: Int,
    var url: String,
    var timestamp: LocalDateTime
)


fun Attachment.toDTO(): AttachmentDTO = AttachmentDTO(
    name,
    contentType,
    length,
    ByteArrayResource(content),
    timestamp
)

fun AttachmentProjection.toStubDTO(): StubAttachmentDTO = StubAttachmentDTO(
    name,
    contentType,
    length,
    id.toString(),
    timestamp
)