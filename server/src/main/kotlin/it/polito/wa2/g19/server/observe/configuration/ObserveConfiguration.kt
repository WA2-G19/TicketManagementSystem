package it.polito.wa2.g19.server.observe.configuration

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import it.polito.wa2.g19.server.observe.AbstractObserveAroundMethodHandler
import it.polito.wa2.g19.server.observe.DefaultObserveAroundMethodHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class ObserveConfiguration {

    @Bean
    @ConditionalOnMissingBean(ObservedAspect::class)
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect = ObservedAspect(observationRegistry)

    @Bean
    @ConditionalOnMissingBean(AbstractObserveAroundMethodHandler::class)
    fun observeAroundMethodHandler(): AbstractObserveAroundMethodHandler = DefaultObserveAroundMethodHandler()
}