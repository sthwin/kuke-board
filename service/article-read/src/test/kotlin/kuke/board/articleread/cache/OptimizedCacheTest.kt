package kuke.board.articleread.cache

import kuke.board.common.dataserializer.DataSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

class OptimizedCacheTest {

    @Test
    fun parseDataTest() {
        parseDataTest<String>(
            data = "data",
            ttlSeconds = 10
        )
        parseDataTest<Long>(
            data = 3L,
            ttlSeconds = 10
        )
        parseDataTest<Int>(
            data = 3,
            ttlSeconds = 10
        )
        parseDataTest<TestClass>(
            data = TestClass(
                name = "test"
            ),
            ttlSeconds = 10
        )
    }

    inline fun <reified T> parseDataTest(
        data: Any,
        ttlSeconds: Long,
    ) {
        val optimizedCache = OptimizedCache.of(
            data = DataSerializer.serialize(data),
            ttl = Duration.ofSeconds(ttlSeconds)
        )
        println("optimizedCache=$optimizedCache")

        val resolvedData = DataSerializer.deserialize(data, T::class.java)
        println("resolvedData=$resolvedData")

        assertThat(resolvedData).isEqualTo(data)
    }

    @Test
    fun isExpiredTest() {
        assertThat(
            OptimizedCache.of(
                data = "data",
                ttl = Duration.ofDays(-10)
            ).isExpired()
        ).isTrue
        assertThat(
            OptimizedCache.of(
                data = "data",
                ttl = Duration.ofDays(10)
            ).isExpired()
        ).isFalse
    }

    data class TestClass(
        val name: String
    )
}
