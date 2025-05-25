package kuke.board.hotarticle.controller

import kuke.board.hotarticle.service.HotArticleService
import kuke.board.hotarticle.service.response.HotArticleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class HotArticleController(
    val hotArticleService: HotArticleService
) {

    /**
     * @param date yyyyMMdd
     */
    @GetMapping("/v1/hot-article/articles/date/{date}")
    fun readAll(
        @PathVariable date: String
    ): List<HotArticleResponse> {
        val dateTime = LocalDateTime.of(
            date.substring(0, 4).toInt(),
            date.substring(4, 6).toInt(),
            date.substring(6, 8).toInt(),
            0,
            0,
            0
        )
        return hotArticleService.readAll(
            dateTime = dateTime
        )
    }
}