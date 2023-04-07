package it.polito.wa2.g19.server.profiles

import org.springframework.web.bind.MethodArgumentNotValidException

class ProfileNotFoundException(message: String): RuntimeException(message)

class DuplicateEmailException(message: String): RuntimeException(message)

