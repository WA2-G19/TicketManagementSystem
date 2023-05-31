package it.polito.wa2.g19.server.observe

import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotNull
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractLogAspect {

    open fun logInfoAround(joinPoint: ProceedingJoinPoint): Any? {
        val logInfo = getLogInfo(joinPoint)
        val log = LoggerFactory.getLogger(logInfo.declaringType)
        logBefore(logInfo, log)
        val obj = joinPoint.proceed()
        logAfter(logInfo, log)
        return obj
    }

    fun logBefore(joinPoint: ProceedingJoinPoint) {
        val (declaringType, className, annotatedMethodName, args) = getLogInfo(joinPoint)
        // this make the logger print the right classType
        val log: Logger = LoggerFactory.getLogger(declaringType)
        log.info(
            "[{}.{}] start ({})", className,
            annotatedMethodName, args
        )
    }

    fun logAfter(joinPoint: ProceedingJoinPoint) {
        val (declaringType, className, annotatedMethodName) = getLogInfo(joinPoint)
        val log: Logger = LoggerFactory.getLogger(declaringType)
        log.info("[{}.{}] end", className, annotatedMethodName)
    }

    @JvmRecord
    private data class LogInfo(
        @field:NotNull @param:NotNull val declaringType: Class<*>,
        @field:NotNull @param:NotNull val className: String,
        @field:NotNull @param:NotNull val annotatedMethodName: String,
        @field:Nullable @param:Nullable val args: Array<Any>
    )

    companion object {
        private fun getLogInfo(joinPoint: ProceedingJoinPoint): LogInfo {
            val signature: Signature = joinPoint.signature
            val declaringType: Class<*> = signature.declaringType
            val className = declaringType.simpleName
            val annotatedMethodName: String = signature.name
            val args = joinPoint.args
            return LogInfo(declaringType, className, annotatedMethodName, args)
        }

        private fun logBefore(logInfo: LogInfo, log: Logger) {
            log.info("[{}.{}] start ({})", logInfo.className, logInfo.annotatedMethodName, logInfo.args)
        }

        private fun logAfter(logInfo: LogInfo, log: Logger) {
            log.info("[{}.{}] end", logInfo.className, logInfo.annotatedMethodName)
        }
    }
}