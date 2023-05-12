package it.polito.wa2.g19.server.ticketing.chat

class MessageNotFoundException: RuntimeException("The message is not found!!!")
class AttachmentNotFoundException: RuntimeException("The attachment is not found!!!")
class NotAllowedToThisMethodException : RuntimeException("Not allowed to use this method!!!")