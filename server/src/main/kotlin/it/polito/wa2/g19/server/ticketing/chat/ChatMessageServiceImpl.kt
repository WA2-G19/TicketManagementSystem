package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.common.Util
import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g19.server.profiles.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toDTO
import it.polito.wa2.g19.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@Service
class ChatMessageServiceImpl(
    private val chatMessageRepository: ChatMessageRepository,
    private val attachmentRepository: AttachmentRepository,
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
) : ChatMessageService {

    override fun getChatMessage(id: Int): ChatMessageOutDTO {
        val message = chatMessageRepository.findByIdOrNull(id) ?: throw MessageNotFoundException()
        val attachmentProjections = attachmentRepository.findByMessage(message)

        return message.toOutDTO(attachmentProjections)

    }

    override fun getChatMessages(): Set<ChatMessageOutDTO> {
        return chatMessageRepository.findAll().map {
            val attachmentsProjection = attachmentRepository.findByMessage(it)
            it.toOutDTO(attachmentsProjection)
        }.toSet()
    }

    override fun insertChatMessage(ticketId: Int,messageToSave: ChatMessageInDTO, files: List<MultipartFile>?):Int {

        val referredTicket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()

        val referredCustomer = customerRepository.findByEmailIgnoreCase(messageToSave.authorEmail) ?: throw ProfileNotFoundException()

        val createdMessage = ChatMessage().apply {
            ticket = referredTicket
            author = referredCustomer
            body = messageToSave.body
        }
        chatMessageRepository.save(createdMessage)

        if (files != null) {
            for (file in files) {
                val creationTime = LocalDateTime.ofInstant(Instant.now(),
                    ZoneId.systemDefault())
                attachmentRepository.save(
                    Attachment().apply {
                        message = createdMessage
                        name = file.originalFilename ?: "file"
                        timestamp =creationTime
                        content = file.bytes
                        length = file.size.toInt()
                        contentType = file.contentType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
                    }
                )
            }
        }
        return createdMessage.getId()!!
    }

    override fun getAttachment(ticketId: Int, attachmentId: Int): AttachmentDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val attachment = attachmentRepository.findByIdOrNull(attachmentId) ?: throw AttachmentNotFoundException()

        return attachment.toDTO()
    //


    }



}