package it.polito.wa2.g19.server.chat

class MessageNotFoundException: RuntimeException("The message is not found!!!")

class NoContentTypeProvided: RuntimeException("The message has not content type!!!")