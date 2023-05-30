package it.polito.wa2.g19.server.observe

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationHandler
import io.micrometer.observation.aop.ObservedAspect.ObservedAspectContext
import org.springframework.stereotype.Component

@Component
class ObserveAroundMethodHandler: AbstractLogAspect(), ObservationHandler<ObservedAspectContext> {

    override fun onStart(context: ObservedAspectContext) {
        logBefore(context.proceedingJoinPoint)
    }

    override fun onStop(context: ObservedAspectContext) {
        logAfter(context.proceedingJoinPoint)
    }

    override fun supportsContext(context: Observation.Context): Boolean = context is ObservedAspectContext
}