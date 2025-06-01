package kuke.board.articleread.cache

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Duration
import java.time.LocalDateTime

data class OptimizedCache(
    val data: String,
    val expiredAt: LocalDateTime, // 로지컬 TTL
) {
    companion object {
        fun of(data: String, ttl: Duration): OptimizedCache {
            return OptimizedCache(
                data = data,
                expiredAt = LocalDateTime.now().plus(ttl)
            )
        }
    }


    @JsonIgnore // 레디스에 저장되지 않도록 함.
    fun isExpired(): Boolean {
        return LocalDateTime.now() > expiredAt
    }


}