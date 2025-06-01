package kuke.board.articleread.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class BoardArticleCountRepository(
    val redisTemplate: StringRedisTemplate
) {
    companion object {
        fun generateKey(boardId: Long): String {
            return "article-read::board-article-count::board::$boardId"
        }
    }

    fun createOrUpdate(boardId: Long, articleCount: Long) {
        redisTemplate.opsForValue().set(generateKey(boardId), articleCount.toString())
    }

    fun read(boardId: Long): Long? {
        return redisTemplate.opsForValue().get(generateKey(boardId))?.toLong()
    }

}