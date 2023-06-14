package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentRepository
import it.polito.wa2.g19.server.profiles.customers.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.profiles.staff.StaffRepository
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toDTO
import it.polito.wa2.g19.server.ticketing.tickets.TicketRepository
import it.polito.wa2.g19.server.ticketing.tickets.TicketService
import it.polito.wa2.g19.server.warranty.Warranty
import it.polito.wa2.g19.server.warranty.WarrantyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
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
    private val chatMessageRepositoryJPA: ChatMessageRepositoryJPA,
    private val attachmentRepository: AttachmentRepository,
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val staffRepository: StaffRepository,
    private val ticketService: TicketService,
    private val warrantyRepository: WarrantyRepository
    ) : ChatMessageService {

    @PreAuthorize("isAuthenticated()")
    override suspend fun getChatMessage(ticketId: Int, chatMessageId: Int): ChatMessageOutDTO {
        ticketService.getTicket(ticketId)
        val message = chatMessageRepository.findById( chatMessageId) ?: throw MessageNotFoundException()
        val attachmentProjections = attachmentRepository.findByMessage(message)
        return message.toOutDTO(attachmentProjections)
    }

    @PreAuthorize("isAuthenticated()")
    override fun getChatMessages(ticketId: Int): Flow<ChatMessageOutDTO> {
        ticketService.getTicket(ticketId)
        return chatMessageRepository.findByTicketId(ticketId)
            .map() {
                it.toOutDTO(attachmentRepository.findByMessage(it))
            }
    }

    @PreAuthorize("isAuthenticated()")
    override fun insertChatMessage(ticketId: Int, messageToSave: ChatMessageInDTO, files: List<MultipartFile>?):Int {

        val referredTicket = ticketRepository.findById(ticketId).get()
        val authorEmail = SecurityContextHolder.getContext().authentication.name
        val profile = customerRepository.findByEmailIgnoreCase(authorEmail)
            ?: staffRepository.findByEmailIgnoreCase(authorEmail)
            ?: throw ProfileNotFoundException()
        val createdMessage = ChatMessage.withAuthor(profile).apply {
            ticket = referredTicket
            body = messageToSave.body
        }
        chatMessageRepositoryJPA.save(createdMessage)

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

    @PreAuthorize("isAuthenticated()")
    override fun getAttachment(ticketId: Int, attachmentId: Int): AttachmentDTO {
        ticketService.getTicket(ticketId)
        val attachment = attachmentRepository.findByIdOrNull(attachmentId) ?: throw AttachmentNotFoundException()
        return attachment.toDTO()
    }
}