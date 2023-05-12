package it.polito.wa2.g19.server.profiles

import it.polito.wa2.g19.server.common.Role
import org.springframework.security.oauth2.jwt.Jwt
import java.security.Principal

class JwtPrincipal: Principal {

    private val email: String
    private val role: Role
    constructor(token: Jwt){
        email = token.getClaim("email")
        role = Role.valueOf(token.getClaim<List<String>>("role")[0])
    }
    override fun getName(): String {
        return this.email
    }

    fun getRole(): Role{
        return this.role
    }
}