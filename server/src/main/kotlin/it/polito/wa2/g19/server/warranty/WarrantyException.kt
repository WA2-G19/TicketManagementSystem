package it.polito.wa2.g19.server.warranty

class WarrantyNotFoundException(): RuntimeException("The warranty you are searching for does not exist")

class WarrantyExpiredException(): RuntimeException("This warranty is expired")

class WarrantyAlreadyActivated(): RuntimeException("The warranty you are trying to activate has already been activated by another user")