package it.polito.wa2.g19.server.profiles.vendors

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class Vendor() {
    @Id
    @Column(nullable = false)
    var id: UUID? = null
    @Column(nullable = false, unique = true)
    var email: String = ""
    @Column(nullable = false, unique = true)
    var businessName: String = ""
    @Column(nullable = false)
    var phoneNumber: String = ""
    @Column(nullable = false)
    var address: String = ""
}