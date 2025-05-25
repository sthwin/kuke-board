package kuke.board.common.outboxmessagerelay

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class MessageRelayCoordinator(
    val redisTemplate: StringRedisTemplate,
    @Value("\${spring.application.name}")
    val applicationName: String,
) {
    val appId: String = UUID.randomUUID().toString()

    companion object {
        const val PING_INTERVAL_SECONDS = 3L
        const val PING_FAILURE_THRESHOLD = 3L
    }

    fun assignShards(): AssignedShard {
        return AssignedShard.of(
            appId = appId,
            appIds = findAppIds(),
            shardCount = MessageRelayConstants.SHARD_COUNT,
        )
    }

    private fun findAppIds(): List<String> {
        return redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)
            ?.toList()
            ?.sorted() ?: emptyList()
    }

    @Scheduled(fixedDelay = PING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    fun ping() {
        redisTemplate.executePipelined { conn ->
            conn as StringRedisConnection
            val key = generateKey()
            conn.zAdd(key, Instant.now().toEpochMilli().toDouble(), appId)
            conn.zRemRangeByScore(
                key,
                Double.NEGATIVE_INFINITY,
                Instant.now().minusSeconds(PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toEpochMilli().toDouble()
            )
            null
        }
    }

    private fun generateKey(): String {
        return "message-relay-coordinator::app-list::${applicationName}"
    }
}