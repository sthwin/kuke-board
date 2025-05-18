package kuke.board.hotarticle.utils

import java.time.Duration
import java.time.LocalTime

object TimeCalculatorUtils {
    fun calculateDurationToMidnight(): Duration {
        val now = java.time.LocalDateTime.now()
        val midnight = now.plusDays(1).with(LocalTime.MIDNIGHT)
        return Duration.between(now, midnight)
    }
}