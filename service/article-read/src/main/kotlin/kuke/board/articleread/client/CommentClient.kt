package kuke.board.articleread.client

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class CommentClient(
    @Value("\${endpoints.kuke-board-comment-service.url}") val commentServiceUrl: String,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var restClient: RestClient

    @PostConstruct
    fun init() {
        restClient = RestClient.create(commentServiceUrl)
    }

    fun count(articleId: Long): Long {
        return runCatching {
            restClient.get().uri("/v2/comments/articles/${articleId}/count")
                .retrieve()
                .body<Long>()!!
        }.getOrElse {
            log.error("[CommentClient.count] articleId=$articleId", it)
            return 0L
        }
    }


}