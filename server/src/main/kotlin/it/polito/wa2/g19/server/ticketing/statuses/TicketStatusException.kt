package it.polito.wa2.g19.server.ticketing.statuses

class InvalidTicketStatusTransitionException(val from: TicketStatusEnum, val to: TicketStatusEnum): RuntimeException("Is not possible to move from ${from.name} state to ${to.name}.")