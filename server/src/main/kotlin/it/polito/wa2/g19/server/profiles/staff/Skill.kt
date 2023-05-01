package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.common.EntityBase
import jakarta.persistence.*

@Table(name = "skill")
@Entity

class Skill: EntityBase<Int>() {

    @Column(nullable = false)
    var name: String = ""

    @ManyToMany(mappedBy = "skills", targetEntity = Staff::class)
    var staff: Set<Staff> = mutableSetOf()

}