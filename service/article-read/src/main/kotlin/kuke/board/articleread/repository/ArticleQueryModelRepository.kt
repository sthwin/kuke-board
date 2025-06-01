package kuke.board.articleread.repository

import kuke.board.common.dataserializer.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleQueryModelRepository(
    val redisTemplate: StringRedisTemplate,
) {

    fun create(articleQueryModel: ArticleQueryModel, ttl: Duration) {
        redisTemplate.opsForValue().set(
            generateKey(articleQueryModel.articleId),
            DataSerializer.serialize(articleQueryModel),
            ttl
        )
    }

    fun update(articleQueryModel: ArticleQueryModel) {
        redisTemplate.opsForValue()
            .setIfPresent(
                generateKey(articleQueryModel.articleId),
                DataSerializer.serialize(articleQueryModel)
            )
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): ArticleQueryModel? {
        return redisTemplate.opsForValue()
            .get(generateKey(articleId))
            ?.let { DataSerializer.deserialize(it) }
    }

    fun readAll(articleIds: List<Long>): Map<Long, ArticleQueryModel> {
        return articleIds.map {
            generateKey(it)
        }.let {
            redisTemplate.opsForValue().multiGet(it)?.filterNotNull()
        }?.associate { valueString ->
            val queryModel = DataSerializer.deserialize<ArticleQueryModel>(valueString)
            queryModel.articleId to queryModel
        } ?: emptyMap()
    }

    companion object {
        private fun generateKey(articleId: Long): String {
            return "article-read::article::${articleId}"
        }
    }
}