package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import kuke.board.articleread.cache.OptimizedCacheable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class ViewClient(
    @Value("\${endpoints.kuke-board-view-service.url}") val viewServiceUrl: String,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var restClient: RestClient

    @PostConstruct
    fun init() {
        restClient = RestClient.create(viewServiceUrl)
    }

    //    @Cacheable(value = ["articleViewCount"], key = "#articleId")
    @OptimizedCacheable(type = "articleViewCount", ttlSeconds = 60 * 60)
    fun count(articleId: Long): Long {
        log.info("[ViewClient.count] articleId=$articleId")
        return runCatching {
            restClient.get().uri("/v1/article-views/articles/${articleId}/count")
                .retrieve()
                .body<Long>()!!
        }.getOrElse {
            log.error("[ViewClient.count] articleId=$articleId", it)
            return 0L
        }
    }


}