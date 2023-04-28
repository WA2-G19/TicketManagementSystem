package it.polito.wa2.g19.server.profiles

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StaffRepository: JpaRepository<Staff, Int> {

    fun findByEmailIgnoreCase(email: String): Staff?;
    fun existsByEmailIgnoreCase(email: String): Boolean;
}