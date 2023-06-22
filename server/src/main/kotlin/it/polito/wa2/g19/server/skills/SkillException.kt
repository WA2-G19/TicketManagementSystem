package it.polito.wa2.g19.server.skills

class SkillNotFoundException: RuntimeException("Skill not found")

class DuplicateSkillException: RuntimeException("A skill with this name already exists")