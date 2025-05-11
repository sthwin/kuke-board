package kuke.board.comment.api

import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentPageResponseV2
import kuke.board.comment.service.response.CommentResponse
import kuke.board.comment.service.response.CommentResponseV2
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.test.Test

@SpringBootTest
class CommentApiV2Test {

    val restClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = create(
            CommentCreateRequestV2(
                articleId = 1,
                content = "my comment1",
                parentPath = null,
                writerId = 1
            )
        )


        val response2 = create(
            CommentCreateRequestV2(
                articleId = 1,
                content = "my comment2",
                parentPath = response1.path,
                writerId = 1
            )
        )


        val response3 = create(
            CommentCreateRequestV2(
                articleId = 1,
                content = "my comment3",
                parentPath = response2.path,
                writerId = 1
            )
        )
        println("${response1.path}(${response1.commentId})")
        println("${response2.path}(${response2.commentId})")
        println("${response3.path}(${response3.commentId}")

        // 출력결과
        // 00002(178068274548592640)
        // 0000200000(178068274779279360)
        // 000020000000000(178068274850582528
    }

    fun create(commentCreateRequest: CommentCreateRequestV2): CommentResponseV2 {
        return restClient.post()
            .uri("/v2/comments")
            .body(commentCreateRequest)
            .retrieve()
            .body<CommentResponseV2>()!!
    }


    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v2/comments/{commentId}", 178068274850582528)
            .retrieve()
            .body<CommentResponseV2>()
        println(response)
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v2/comments/{commentId}", 178068274850582528)
            .retrieve()
            .body<Unit>()
    }

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v2/comments?articleId=1&pageSize=10&page=1")
            .retrieve()
            .body<CommentPageResponseV2>()

        println("response.commentCount:${response?.commentCount}")
        response?.comments?.forEach {
            println("commentId:${it.commentId}")
        }
    }

//    commentId:178102657915314176
//    commentId:178102658024366101
//    commentId:178102658024366108
//    commentId:178102658028560429
//    commentId:178102658032754691
//    commentId:178102658032754705
//    commentId:178102658032754713
//    commentId:178102658032754717
//    commentId:178102658032754723
//    commentId:178102658032754728

    @Test
    fun readAllInfiniteScroll() {
        val response1 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body<List<CommentResponseV2>>()

        println("first page")
        response1?.forEach {
            println("commentId:${it.commentId}")
        }


        val lastPath = response1?.last()?.path
        if (lastPath == null) return

        val response2 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=${lastPath}")
            .retrieve()
            .body<List<CommentResponseV2>>()


        println("second page")
        response2?.forEach {
            println("commentId:${it.commentId}")
        }
    }


    @Test
    fun countTest() {
        val response = create(
            CommentCreateRequestV2(
                articleId = 2,
                content = "my comment1",
                parentPath = null,
                writerId = 1
            )
        )

        val count1 = restClient.get()
            .uri("/v2/comments/articles/${response.articleId}/count")
            .retrieve()
            .body<Long>()
        println("count1=$count1")

        restClient.delete()
            .uri("/v2/comments/{commentId}", response.commentId)
            .retrieve()
            .body<Unit>()

        val count2 = restClient.get()
            .uri("/v2/comments/articles/${response.articleId}/count")
            .retrieve()
            .body<Long>()
        println("count2=$count2")
    }
}