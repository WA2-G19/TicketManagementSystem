package it.polito.wa2.g19.server.warranty

import it.polito.wa2.g19.server.products.Product
import it.polito.wa2.g19.server.profiles.customers.Customer
import it.polito.wa2.g19.server.profiles.vendors.Vendor
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "warranty")
class Warranty() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    var id: UUID? = null
    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var product: Product
    @ManyToOne
    @JoinColumn(nullable = false)
    lateinit var vendor: Vendor
    @ManyToOne
    @JoinColumn(nullable = true)
    var customer: Customer? = null
    @CreationTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var creationTimestamp: LocalDateTime = LocalDateTime.now()
    @Column(nullable = false)
    var duration: Duration = Duration.ZERO
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    var activationTimestamp: LocalDateTime? = null
}