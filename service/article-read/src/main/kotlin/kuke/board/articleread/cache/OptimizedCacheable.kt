package kuke.board.articleread.cache


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OptimizedCacheable(
    val type: String,
    val ttlSeconds: Long,
)