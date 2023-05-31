package it.polito.wa2.g19.server.observe

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect

@Aspect
class DefaultLogAspect: AbstractLogAspect() {
    override fun logInfoAround(joinPoint: ProceedingJoinPoint): Any? {
        return super.logInfoAround(joinPoint)
    }
}