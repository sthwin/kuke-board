package kuke.board.articleread.cache

import kuke.board.common.dataserializer.DataSerializer
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class OptimizedCacheManager(
    val redisTemplate: StringRedisTemplate,
    val optimizedCacheLockProvider: OptimizedCacheLockProvider,
) {

    companion object {
        const val DELIMITER = "::"
    }

    fun <T : Any> process(
        type: String,
        ttlSeconds: Long,
        vararg args: Any,
        returnType: Class<T>,
        originDataSupplier: OptimizedCacheOriginDataSupplier<T>
    ): T {
        val key = generateKey(type, *args)

        // 캐시에 데이터가 있는지 확인한다.
        val cachedData = redisTemplate.opsForValue().get(key)

        // 캐시에 데이터가 없으면 원본 데이터를 요청하고, 조회했던 데이터를 캐시에 저장한다.
        // 멀티스레딩 환경에서 최초 조회시(레디스에 데이터가 없을 경우)에는 여러번 원본 데이터를 요청할 수 있다.
        if (cachedData == null) {
            return refresh(
                key = key,
                ttlSeconds = ttlSeconds,
                originDataSupplier = originDataSupplier
            )
        }

        val optimizedCache = DataSerializer.deserialize<OptimizedCache>(cachedData)
        // 조회된 캐시데이터가 만료되지 않았으면 조회했던 데이터를 반환한다.
        if (!optimizedCache.isExpired()) {
            return DataSerializer.deserialize(optimizedCache.data, returnType)
        }

        // 조회된 캐시데이터가 만료되었다면, 락을 잡는다.
        // 락을 잡지 못했으면, 우선 기존 데이터를 반환한다.
        if (!optimizedCacheLockProvider.lock(key)) {
            return DataSerializer.deserialize(optimizedCache.data, returnType)
        }

        // 조회된 캐시데이터가 만료되었다면, 작을 잡고 원본 데이터 조회 및 레디스에 저장 후, 조회된 원본데이터를 반환한다.
        return try {
            refresh(
                key = key,
                ttlSeconds = ttlSeconds,
                originDataSupplier = originDataSupplier
            )
        } finally {
            optimizedCacheLockProvider.unlock(key)
        }

    }

    private fun <T : Any> refresh(
        key: String,
        ttlSeconds: Long,
        originDataSupplier: OptimizedCacheOriginDataSupplier<T>
    ): T {
        val result = originDataSupplier.get()

        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)
        val optimizedCache = OptimizedCache.of(
            data = DataSerializer.serialize(result as Any),
            ttl = optimizedCacheTTL.logicalTTL
        )

        redisTemplate.opsForValue().set(
            key,
            DataSerializer.serialize(optimizedCache),
            optimizedCacheTTL.physicalTTL
        )

        return result
    }

    private fun generateKey(prefix: String, vararg args: Any): String {
        return prefix + DELIMITER + args.joinToString(DELIMITER)
    }
}