package kuke.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
class ArticleCreatedTimeRepository(
    val redisTemplate: StringRedisTemplate
) {

    companion object {
        fun generateKey(articleId: Long): String {
            return "hot-article::article::${articleId}::created-time"
        }
    }

    /**
     * 좋아요 이벤트를 받았는데, 이 이벤트에 대한 게시글이 오늘의 게시글인지 확인하려면, 게시글 서비스에 조회가 필요하다.
     * 하지만 게시글 생성 시간을 저장하고 있으면, 게시글이 오늘 만들어졌었는지 게시글 API를 호출하지 않아도 알 수 있게 된다.
     */
    fun createOrUpdate(articleId: Long, createdAt: LocalDateTime, ttl: Duration) {
        redisTemplate.opsForValue().set(
            generateKey(articleId),
            createdAt.toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
            ttl
        )
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): LocalDateTime? {
        return redisTemplate.opsForValue().get(generateKey(articleId))?.toLong()
            ?.let {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(it),
                    ZoneOffset.UTC
                )
            }
    }
}