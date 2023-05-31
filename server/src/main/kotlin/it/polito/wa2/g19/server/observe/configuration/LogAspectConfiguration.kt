package it.polito.wa2.g19.server.observe.configuration

import it.polito.wa2.g19.server.observe.AbstractLogAspect
import it.polito.wa2.g19.server.observe.DefaultLogAspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class LogAspectConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun defaultLogAspect(): AbstractLogAspect = DefaultLogAspect()
}