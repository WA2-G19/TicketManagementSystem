package it.polito.wa2.g19.server.profiles

import jakarta.persistence.Entity

@Entity
abstract class Staff(): Profile() {
}

@Entity
class Manager(): Staff() {
}

@Entity
class Expert(): Staff() {
}