package it.polito.wa2.g19.server.profiles.staff

import jakarta.persistence.*

@Table(name = "skill")
@Entity

class Skill {

    @Id
    @Column(nullable = false)
    var name: String = ""

    @ManyToMany(mappedBy = "skills", targetEntity = Staff::class)
    var staff: Set<Staff> = mutableSetOf()

}