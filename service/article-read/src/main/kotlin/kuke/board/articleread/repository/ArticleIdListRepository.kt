package kuke.board.articleread.repository

import org.springframework.data.domain.Range
import org.springframework.data.redis.connection.Limit
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleIdListRepository(
    val redisTemplate: StringRedisTemplate
) {

    fun generateKey(boardId: Long): String {
        return "article-read::board::${boardId}::article-list"
    }

    fun add(boardId: Long, articleId: Long, limit: Long) {
        redisTemplate.executePipelined { conn ->
            conn as StringRedisConnection
            val key = generateKey(boardId)
            conn.zAdd(key, 0.0, toPaddedString(articleId)) // 스코어가 동일하면 value를 기준으로 정렬됨
            conn.zRemRange(key, 0, -limit - 1)
            null
        }
    }

    fun delete(boardId: Long, articleId: Long) {
        redisTemplate.opsForZSet().remove(
            generateKey(boardId),
            toPaddedString(articleId)
        )
    }

    fun readAll(boardId: Long, offset: Long, limit: Long): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRange(generateKey(boardId), offset, offset + limit - 1)
            ?.map { it.toLong() } ?: emptyList()
    }

    fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, limit: Long): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeByLex(
                generateKey(boardId),
                if (lastArticleId == null) Range.unbounded()
                else Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId))),
                Limit.limit().count(limit.toInt())
            )?.map { it.toLong() } ?: emptyList()
    }

    fun toPaddedString(articleId: Long): String {
        return articleId.toString().padStart(19, '0')
    }
}