package it.polito.wa2.g19.server.observe

import jakarta.validation.constraints.NotNull
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractLogAspect {
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

    private data class LogInfo(
        @field:NotNull @param:NotNull val declaringType: Class<*>,
        @field:NotNull @param:NotNull val className: String,
        @field:NotNull @param:NotNull val annotatedMethodName: String,
        val args: Array<Any>?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LogInfo

            if (declaringType != other.declaringType) return false
            if (className != other.className) return false
            if (annotatedMethodName != other.annotatedMethodName) return false
            if (args != null) {
                if (other.args == null) return false
                if (!args.contentEquals(other.args)) return false
            } else if (other.args != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = declaringType.hashCode()
            result = 31 * result + className.hashCode()
            result = 31 * result + annotatedMethodName.hashCode()
            result = 31 * result + (args?.contentHashCode() ?: 0)
            return result
        }
    }

    companion object {
        private fun getLogInfo(joinPoint: ProceedingJoinPoint): LogInfo {
            val signature: Signature = joinPoint.signature
            val declaringType: Class<*> = signature.declaringType
            val className = declaringType.simpleName
            val annotatedMethodName: String = signature.name
            val args = joinPoint.args
            return LogInfo(declaringType, className, annotatedMethodName, args)
        }
    }
}