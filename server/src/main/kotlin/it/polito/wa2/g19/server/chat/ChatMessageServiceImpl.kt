package it.polito.wa2.g19.server.chat

import it.polito.wa2.g19.server.attachments.Attachment
import it.polito.wa2.g19.server.attachments.AttachmentRepository
import it.polito.wa2.g19.server.profiles.CustomerRepository
import it.polito.wa2.g19.server.profiles.ProfileNotFoundException
import it.polito.wa2.g19.server.tickets.TicketNotFoundException
import it.polito.wa2.g19.server.tickets.TicketRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths

@Service
class ChatMessageServiceImpl(
    private val chatMessageRepository: ChatMessageRepository,
    private val attachmentRepository: AttachmentRepository,
    private val ticketRepository: TicketRepository,
    private val customerRepository: CustomerRepository,
) : ChatMessageService {

    override fun getChatMessage(id: Int): ChatMessageDTO {
        val message = chatMessageRepository.findByIdOrNull(id) ?: throw MessageNotFoundException()
        return message.toDTO()
    }

    override fun getChatMessages(): Set<ChatMessageDTO> {
        return chatMessageRepository.findAll().map { it.toDTO() }.toSet()
    }

    override fun insertChatMessage(messageToSave: ChatMessageDTO, files: List<MultipartFile>) {

        val referredTicket = ticketRepository.findByIdOrNull(messageToSave.ticketId) ?: throw TicketNotFoundException()
        val referredCustomer = customerRepository.findByIdOrNull(messageToSave.authorId) ?: throw ProfileNotFoundException()

        val createdMessage = ChatMessage().apply {
            ticket = referredTicket
            author = referredCustomer
            body = messageToSave.body
        }
        chatMessageRepository.save(createdMessage)

        for (file in files) {
            attachmentRepository.save(
                Attachment().apply {
                    message = createdMessage
                    content = file.bytes
                    length = file.size.toInt()
                    contentType = file.contentType ?: ""
                }
            )
        }
    }

}