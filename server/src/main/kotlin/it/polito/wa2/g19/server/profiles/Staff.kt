package it.polito.wa2.g19.server.profiles

import jakarta.persistence.Entity

@Entity
abstract class Staff(): Profile() {

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