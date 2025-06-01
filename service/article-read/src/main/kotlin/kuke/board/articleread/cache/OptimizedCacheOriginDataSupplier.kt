package kuke.board.articleread.cache

fun interface OptimizedCacheOriginDataSupplier<T : Any> {
    fun get(): T
}