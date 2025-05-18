package kuke.board.hotarticle.repository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class HotArticleListRepository(
    val redisTemplate: StringRedisTemplate
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        fun generateKey(date: LocalDateTime): String {
            return TIME_FORMATTER.format(date).also {
                "hot-article::list::${it}"
            }
        }
    }

    fun add(articleId: Long, time: LocalDateTime, score: Double, limit: Long, ttl: Duration) {
        redisTemplate.executePipelined { conn ->
            conn as StringRedisConnection
            val key = generateKey(time)
            conn.zAdd(key, score, articleId.toString())
            conn.zRemRange(key, 0, -limit - 1)
            conn.expire(key, ttl.toSeconds())
            null
        }
    }

    fun remove(articleId: Long, time: LocalDateTime) {
        redisTemplate.opsForZSet()
            .remove(generateKey(time), articleId.toString())
    }

    fun readAll(dateStr: LocalDateTime): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(generateKey(dateStr), 0, -1)
            ?.map {
                logger.info("[HotArticleListRepository.readAll]: $it")
                it.value!!.toLong()
            } ?: emptyList()
    }
}