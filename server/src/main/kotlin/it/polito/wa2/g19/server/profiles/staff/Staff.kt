package it.polito.wa2.g19.server.profiles.staff

import it.polito.wa2.g19.server.profiles.Profile
import jakarta.persistence.*

@Entity
abstract class Staff(): Profile() {

    @ManyToMany
    @JoinTable(name="staff_skill",
        joinColumns = [JoinColumn(name="staff_id")],
        inverseJoinColumns = [JoinColumn(name="skill_id")]
    )
    @Basic(fetch = FetchType.EAGER)
    val skills: MutableSet<Skill> = mutableSetOf()
}

@Entity
class Manager(): Staff() {



    constructor(email: String, name: String, surname: String): this(){
        super.email = email
        super.name = name
        super.surname = surname
    }
}

@Entity
class Expert(): Staff() {
    constructor(email: String, name: String, surname: String): this(){
        super.email = email
        super.name = name
        super.surname = surname
    }
}