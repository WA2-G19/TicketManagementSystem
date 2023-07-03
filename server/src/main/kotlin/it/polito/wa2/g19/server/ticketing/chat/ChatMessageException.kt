package it.polito.wa2.g19.server.ticketing.chat

class MessageNotFoundException: RuntimeException("The message is not found!!!")
class AttachmentNotFoundException: RuntimeException("The attachment is not found!!!")
class AuthorAndUserAreDifferentException: RuntimeException("Author of the message and user must be the same!!!")
class ChatClosedException: RuntimeException("Cannot insert messages. Ticket is closed!!!")