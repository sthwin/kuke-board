package kuke.board.articleread.cache

import java.time.Duration

/**
 * 로지컬과 피지컬 TTL을 계산해줌.
 */
class OptimizedCacheTTL(
    val logicalTTL: Duration,
    val physicalTTL: Duration
) {

    companion object {
        const val PHYSICAL_TTL_DELAY_SECONDS = 5L

        fun of(ttlSeconds: Long): OptimizedCacheTTL {
            return with(Duration.ofSeconds(ttlSeconds)) {
                OptimizedCacheTTL(
                    logicalTTL = this,
                    physicalTTL = this.plusSeconds(PHYSICAL_TTL_DELAY_SECONDS)
                )
            }
        }
    }
}