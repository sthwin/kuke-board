package kuke.board.articleread.cache

import kuke.board.articleread.cache.OptimizedCacheTTL.Companion.PHYSICAL_TTL_DELAY_SECONDS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration

class OptimizedCacheTTLTest {

    @Test
    fun ofTest() {
        // given
        val ttl = 10L

        // when
        val optimizedCacheTTL = OptimizedCacheTTL.of(ttl)

        // then
        assertEquals(Duration.ofSeconds(ttl), optimizedCacheTTL.logicalTTL)
        assertEquals(Duration.ofSeconds(ttl + PHYSICAL_TTL_DELAY_SECONDS), optimizedCacheTTL.physicalTTL)
    }
}