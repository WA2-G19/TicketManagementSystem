package it.polito.wa2.g19.server.attachments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository: JpaRepository<Attachment, Int> {
}