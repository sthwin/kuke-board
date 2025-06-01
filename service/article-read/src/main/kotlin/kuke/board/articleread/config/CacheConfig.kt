package kuke.board.articleread.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        return RedisCacheManager.builder(connectionFactory)
            .withInitialCacheConfigurations(
                mapOf(
                    "articleViewCount" to RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(1)),
                )
            )
            .build()
    }
}