package kuke.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleViewDistributedLockRepository(
    private val stringRedisTemplate: StringRedisTemplate
) {

    companion object {
        const val KEY_FORMAT = "view::article::%s::user::%s::lock"

        fun generateKey(articleId: Long, userId: Long): String = "view::article::$articleId::user::$userId::lock"
    }

    fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean {
        val key = generateKey(articleId, userId)
        return stringRedisTemplate.opsForValue().setIfAbsent(key, "1", ttl) ?: false
    }
}