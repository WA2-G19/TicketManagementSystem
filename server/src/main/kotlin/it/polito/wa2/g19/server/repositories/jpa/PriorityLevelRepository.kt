package it.polito.wa2.g19.server.repositories.jpa

import it.polito.wa2.g19.server.ticketing.statuses.PriorityLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PriorityLevelRepository: JpaRepository<PriorityLevel, String> {

}