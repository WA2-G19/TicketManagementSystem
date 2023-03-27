package it.polito.wa2.g19.server.profiles

class ProfileNotFoundException(message: String): RuntimeException(message)

class DuplicateEmailException(message: String): RuntimeException(message)
