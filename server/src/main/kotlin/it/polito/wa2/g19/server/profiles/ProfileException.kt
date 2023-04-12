package it.polito.wa2.g19.server.profiles

class ProfileNotFoundException(): RuntimeException("There is no profile associated to this email")

class DuplicateEmailException(): RuntimeException("There is already an email associated to this profile")

class NotMatchingEmailException(): RuntimeException("The email in the path does not match the email in the body of the request")