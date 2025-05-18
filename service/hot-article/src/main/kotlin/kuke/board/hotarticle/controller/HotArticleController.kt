package kuke.board.hotarticle.controller

import kuke.board.hotarticle.service.HotArticleService
import kuke.board.hotarticle.service.response.HotArticleResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class HotArticleController(
    val hotArticleService: HotArticleService
) {

    @GetMapping("/v1/hot-article/articles/date/{date}")
    fun readAll(
        @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") date: LocalDateTime
    ): List<HotArticleResponse> {
        return hotArticleService.readAll(
            dateTime = date
        )
    }
}