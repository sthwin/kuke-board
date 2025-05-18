package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleCommentCountRepository(
    val redisTemplate: StringRedisTemplate
) {

    companion object {
        fun generateKey(articleId: Long): String {
            return "hot-article::article::${articleId}::comment-count"
        }
    }

    fun createOrUpdate(articleId: Long, commentCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(
            generateKey(articleId),
            commentCount.toString(),
            ttl
        )
    }

    fun read(articleId: Long): Long {
        return redisTemplate.opsForValue().get(generateKey(articleId))?.toLong() ?: 0
    }
}