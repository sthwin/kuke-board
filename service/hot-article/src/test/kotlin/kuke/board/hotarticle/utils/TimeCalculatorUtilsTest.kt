package kuke.board.hotarticle.utils

import org.junit.jupiter.api.Test

class TimeCalculatorUtilsTest {

    @Test
    fun calculateDurationToMidnight() {
        val duration = TimeCalculatorUtils.calculateDurationToMidnight()
        println(duration.toHours())
    }
}