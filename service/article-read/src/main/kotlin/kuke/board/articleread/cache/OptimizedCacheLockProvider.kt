package kuke.board.articleread.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheLockProvider(
    val restTemplate: StringRedisTemplate
) {

    companion object {
        const val KEY_PREFIX = "optimized-cache-lock::"
        val LOCK_TTL: Duration = Duration.ofSeconds(3)
    }

    fun lock(key: String): Boolean {
        return restTemplate.opsForValue()
            .setIfAbsent(
                "$KEY_PREFIX$key",
                "",
                LOCK_TTL
            ) ?: false
    }

    fun unlock(key: String) {
        restTemplate.delete("$KEY_PREFIX$key")
    }
}