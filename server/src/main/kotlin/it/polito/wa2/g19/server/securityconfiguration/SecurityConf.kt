package it.polito.wa2.g19.server.securityconfiguration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Component

@EnableWebSecurity
@Component
@EnableMethodSecurity
class ResourceServerConfig {


    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private lateinit var issuerUri: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .csrf().disable()
            // Cors options
            // ================================== //
            .cors().disable()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/index.html","/", "/static/**", "/manifest.json", "/signup").permitAll()
            // ================================== //
            // Actuator API
            // ================================== //
            .requestMatchers("/actuator/prometheus")
            .hasRole("Prometheus")
            // ================================== //
            // Products API
            // ================================== //
            .requestMatchers(HttpMethod.GET, "/API/products**")
                .authenticated()
            .requestMatchers(HttpMethod.POST, "/API/products")
                .hasRole("Manager")
            .requestMatchers("/API/products/**")
                .authenticated()
            // ================================== //
            // Staff API
            // ================================== //
            .requestMatchers("/API/staff/*")
                .hasAnyRole("Expert", "Manager")
            // ================================== //
            // Profiles API
            // ================================== //
            .requestMatchers("/API/profiles")
                .hasRole("Manager")
            .requestMatchers("/API/profiles/*")
                .authenticated()
            // ================================== //
            // Vendor API
            // ================================== //
            .requestMatchers("/API/vendor/")
                .hasRole("Manager")
            .requestMatchers("/API/vendor/*")
                .hasAnyRole("Manager", "Vendor")
            // ================================== //
            // Tickets API
            // ================================== //
            .requestMatchers("/API/tickets/*/chat-messages/**")
                .authenticated()
            .requestMatchers("/API/tickets/chat-messages/unread")
                .hasAnyRole("Manager", "Expert", "Client")
            .requestMatchers("/API/stats/**")
                .hasRole("Manager")
            .requestMatchers("/API/tickets/*")
                .authenticated()
            .requestMatchers(HttpMethod.POST, "/API/tickets")
                .hasRole("Client")
            .requestMatchers(HttpMethod.GET, "/API/tickets**")
                .hasAnyRole("Manager", "Client", "Expert")
            // ================================== //
            // Warranty API
            // ================================== //
            .requestMatchers(HttpMethod.POST,"/API/warranty")
                .hasRole("Vendor")
            .requestMatchers(HttpMethod.GET, "/API/warranty")
                .hasAnyRole("Manager", "Vendor", "Client")
            .requestMatchers("/API/warranty/*/activate")
                .hasRole("Client")
            .requestMatchers(HttpMethod.GET, "/API/warranty/*")
                .hasAnyRole("Client", "Vendor", "Manager")
            .requestMatchers(HttpMethod.DELETE, "/API/warranty/*")
                .hasRole("Vendor")
            // ================================== //
            // Skill API
            // ================================== //
            .requestMatchers("/API/skill")
                .hasRole("Manager")
            // ================================== //
            // Login/Sign up API
            // ================================== //
            .requestMatchers("/API/login")
                .permitAll()
            .requestMatchers("/API/signup")
                .permitAll()
            .and()
            .formLogin().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .oauth2ResourceServer()
            .jwt()


        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role")
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        jwtAuthenticationConverter.setPrincipalClaimName("email")
        return jwtAuthenticationConverter
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(issuerUri).build()
    }
}