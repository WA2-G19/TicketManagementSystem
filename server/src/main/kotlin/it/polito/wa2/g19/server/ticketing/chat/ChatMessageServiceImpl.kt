package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toDTO
import it.polito.wa2.g19.server.ticketing.tickets.TicketNotFoundException
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@Service
@Transactional

class ChatMessageServiceImpl(
    private val chatMessageRepository: ChatMessageRepository,
    private val attachmentRepository: AttachmentRepository,
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
) : ChatMessageService {

    override fun getChatMessage(ticketId: Int, chatMessageId: Int): ChatMessageOutDTO {
        ticketRepository.existsById(ticketId).let {if(!it) throw TicketNotFoundException()}

        val message = chatMessageRepository.findByTicketIdAndId(ticketId, chatMessageId) ?: throw MessageNotFoundException()
        val attachmentProjections = attachmentRepository.findByMessage(message)
        return message.toOutDTO(attachmentProjections)

    }

    override fun getChatMessages(ticketId: Int): Set<ChatMessageOutDTO> {
        if (!ticketRepository.existsById(ticketId))
            throw TicketNotFoundException()
        return chatMessageRepository.findByTicketId(ticketId)!!
            .map {
                it.toOutDTO(attachmentRepository.findByMessage(it))
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
        ticketRepository.findByIdOrNull(ticketId) ?: throw TicketNotFoundException()
        val attachment = attachmentRepository.findByIdOrNull(attachmentId) ?: throw AttachmentNotFoundException()

        return attachment.toDTO()
    }
}