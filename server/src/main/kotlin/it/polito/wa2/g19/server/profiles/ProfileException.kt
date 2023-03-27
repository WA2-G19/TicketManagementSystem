package it.polito.wa2.g19.server.profiles

class ProfileNotFoundException(message: String): Exception(message)

class DuplicateEmailException(message: String): Exception(message)
