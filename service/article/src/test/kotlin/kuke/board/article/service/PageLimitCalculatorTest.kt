package kuke.board.article.service

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class PageLimitCalculatorTest {

    @Test
    fun calculatePageLimitTest() {
        calculatePageLimitTest(
            page = 1,
            pageSize = 30,
            movablePageCount = 10,
            expected = 301
        )

        calculatePageLimitTest(
            page = 7,
            pageSize = 30,
            movablePageCount = 10,
            expected = 301
        )

        calculatePageLimitTest(
            page = 10,
            pageSize = 30,
            movablePageCount = 10,
            expected = 301
        )

        calculatePageLimitTest(
            page = 11,
            pageSize = 30,
            movablePageCount = 10,
            expected = 601
        )

        calculatePageLimitTest(
            page = 12,
            pageSize = 30,
            movablePageCount = 10,
            expected = 601
        )
    }

    fun calculatePageLimitTest(
        page: Long,
        pageSize: Long,
        movablePageCount: Long,
        expected: Long
    ) {
        val result = PageLimitCalculator.calculatePageLimit(
            page = page,
            pageSize = pageSize,
            movablePageCount = movablePageCount
        )
        assertEquals(expected, result)
    }
}