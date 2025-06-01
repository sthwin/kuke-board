package kuke.board.articleread.cache

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OptimizedCacheAspect(
    val optimizedCacheManager: OptimizedCacheManager
) {

    @Around("@annotation(kuke.board.articleread.cache.OptimizedCacheable)")
    fun around(joinPoint: ProceedingJoinPoint): Any {
        findAnnotation(joinPoint).let {
            return optimizedCacheManager.process(
                type = it.type,
                ttlSeconds = it.ttlSeconds,
                args = joinPoint.args,
                returnType = findReturnType(joinPoint) as Class<Any>,
                originDataSupplier = {
                    joinPoint.proceed() as Any
                }
            )
        }
    }

    private fun findAnnotation(joinPoint: ProceedingJoinPoint): OptimizedCacheable {
        return (joinPoint.signature as MethodSignature)
            .method
            .getAnnotation(OptimizedCacheable::class.java)
    }

    private fun findReturnType(joinPoint: ProceedingJoinPoint): Class<*> {
        return (joinPoint.signature as MethodSignature)
            .method
            .returnType
    }
}