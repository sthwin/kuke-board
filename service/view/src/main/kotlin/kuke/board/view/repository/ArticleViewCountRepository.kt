package kuke.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
data class ArticleViewCountRepository(
    val stringRedisTemplate: StringRedisTemplate
) {
    companion object {
        fun generateKey(articleId: Long): String {
            return "view::article::$articleId::view_count"
        }
    }

    fun read(articleId: Long): Long {
        return stringRedisTemplate.opsForValue().get(generateKey(articleId))?.toLong() ?: 0L
    }

    fun increase(articleId: Long): Long {
        return stringRedisTemplate.opsForValue().increment(generateKey(articleId)) ?: 0L
    }
}