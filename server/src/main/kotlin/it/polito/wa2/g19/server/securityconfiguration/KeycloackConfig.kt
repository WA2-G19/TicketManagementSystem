package it.polito.wa2.g19.server.securityconfiguration

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class KeycloakConfig {

    @Value("\${keycloak.admin.realm}")
    private val realm: String? = null

    @Value("\${keycloak.admin.username}")
    private val username: String? = null

    @Value("\${keycloak.admin.password}")
    private val password: String? = null

    @Value("\${keycloak.auth-server-url}")
    private val authServerUrl: String? = null
    @Bean
    fun keycloak(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(authServerUrl)
            .realm(realm)
            .grantType(OAuth2Constants.PASSWORD)
            .clientId("TicketManagementSystem")
            .clientSecret("eoM7Xo7Ft93eyph81RnfSiNcJ9Cawvfw")
            .username(username)
            .password(password)
            .resteasyClient(ResteasyClientBuilderImpl().connectionPoolSize(10).build())
            .build()
    }

}
