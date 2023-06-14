package it.polito.wa2.g19.server.ticketing.chat

import it.polito.wa2.g19.server.ticketing.attachments.Attachment
import it.polito.wa2.g19.server.repositories.jpa.AttachmentRepository
import it.polito.wa2.g19.server.repositories.jpa.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.repositories.jpa.StaffRepository
import it.polito.wa2.g19.server.repositories.reactive.ChatMessageRepository
import it.polito.wa2.g19.server.ticketing.attachments.AttachmentDTO
import it.polito.wa2.g19.server.ticketing.attachments.toDTO
import it.polito.wa2.g19.server.repositories.jpa.TicketRepository
import it.polito.wa2.g19.server.ticketing.tickets.TicketService
import it.polito.wa2.g19.server.repositories.jpa.WarrantyRepository
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
@Transactional("connectionFactoryTransactionManager")

class ChatMessageServiceImpl(
    private val chatMessageRepository: ChatMessageRepository,
    private val attachmentRepository: AttachmentRepository,
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
    private val staffRepository: StaffRepository,
    private val ticketService: TicketService,
    ) : ChatMessageService {

    @PreAuthorize("isAuthenticated()")
    override suspend fun getChatMessage(ticketId: Int, chatMessageId: Int): ChatMessageOutDTO {
        val message = chatMessageRepository.findById(chatMessageId) ?: throw MessageNotFoundException()
        val attachmentProjections = attachmentRepository.findByMessage(message)
        val authorEmail = if(message.customerAuthorId != null){
            customerRepository.findById(message.customerAuthorId!!).get().email
        } else{
            staffRepository.findById(message.staffAuthorId!!).get().email

        }
        return message.toOutDTO(attachmentProjections, authorEmail)
    }

    @PreAuthorize("isAuthenticated()")
    override suspend fun getChatMessages(ticketId: Int): Flow<ChatMessageOutDTO> {
        return chatMessageRepository.findByTicketId(ticketId)
            .map {
                val authorEmail = if(it.customerAuthorId != null){
                    customerRepository.findById(it.customerAuthorId!!).get().email
                } else{
                    staffRepository.findById(it.staffAuthorId!!).get().email

                }
                it.toOutDTO(attachmentRepository.findByMessage(it), authorEmail)
            }
    }

    @PreAuthorize("isAuthenticated()")
    override suspend fun insertChatMessage(ticketId: Int, messageToSave: ChatMessageInDTO, files: List<MultipartFile>?):Int {

        val referredTicket = ticketRepository.findById(ticketId).get()
        val authorEmail = SecurityContextHolder.getContext().authentication.name
        val profile = customerRepository.findByEmailIgnoreCase(authorEmail)
            ?: staffRepository.findByEmailIgnoreCase(authorEmail)
            ?: throw ProfileNotFoundException()
        val createdMessage = ChatMessage.withAuthor(profile, referredTicket, messageToSave.body)
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
        return createdMessage.id!!.toInt()
    }

    @PreAuthorize("isAuthenticated()")
    override fun getAttachment(ticketId: Int, attachmentId: Int): AttachmentDTO {
        ticketService.getTicket(ticketId)
        val attachment = attachmentRepository.findByIdOrNull(attachmentId) ?: throw AttachmentNotFoundException()
        return attachment.toDTO()
    }
}