package it.polito.wa2.g19.server.profiles.staff

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StaffRepository: JpaRepository<Staff, Int> {

    fun findByEmailIgnoreCase(email: String): Staff?
    fun existsByEmailIgnoreCase(email: String): Boolean

    @Query(value="select e from Expert e")
    fun findAllExpert(): Array<Staff>
}