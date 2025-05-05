package kuke.board.comment.api

import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.test.Test

@SpringBootTest
class CommentApiTest {

    val restClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = createComment(
            CommentCreateRequest(
                articleId = 1,
                content = "my comment1",
                parentCommentId = null,
                writerId = 1,
            )
        )
        val response2 = createComment(
            CommentCreateRequest(
                articleId = 1,
                content = "my comment2",
                parentCommentId = response1.commentId,
                writerId = 1,
            )
        )
        val response3 = createComment(
            CommentCreateRequest(
                articleId = 1,
                content = "my comment3",
                parentCommentId = response1.commentId,
                writerId = 1,
            )
        )

        println("commentId=${response1.commentId}")
        println("\tcommentId=${response2.commentId}")
        println("\tcommentId=${response3.commentId}")

//        commentId=177612890797182976
//        commentId=177612891296305152
//        commentId=177612891350831104

    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v1/comments/177612890797182976")
            .retrieve()
            .body<CommentResponse>()

        println(response)
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v1/comments/177612891350831104")
            .retrieve()
            .body<Unit>()
    }

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v1/comments?articleId=1&page=1&pageSize=10")
            .retrieve()
            .body<CommentPageResponse>()

        println("response getCommentCount=${response?.commentCount}")
        response?.comments?.forEach {
            if (it.parentCommentId != it.commentId) {
                print("\t")
            }
            println(it)
        }

        // 1번 페이지 수행결과
//        CommentResponse(commentId=177684112621936640, content=content, parentCommentId=177684112621936640, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491072, content=content, parentCommentId=177684112621936640, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491073, content=content, parentCommentId=177684112655491073, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491074, content=content, parentCommentId=177684112655491073, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491075, content=content, parentCommentId=177684112655491075, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491076, content=content, parentCommentId=177684112655491075, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491077, content=content, parentCommentId=177684112655491077, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491078, content=content, parentCommentId=177684112655491077, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491079, content=content, parentCommentId=177684112655491079, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
//        CommentResponse(commentId=177684112655491080, content=content, parentCommentId=177684112655491079, articleId=1, writerId=1, deleted=false, createdAt=2025-05-05T16:33:14)
    }

    @Test
    fun readAllInfiniteScroll() {
        val response1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body<List<CommentResponse>>()

        println("firstpage")
        response1?.forEach {
            if (it.parentCommentId != it.commentId) {
                print("\t")
            }
            println(it.commentId)
        }

        val lastCommentId = response1?.last()?.commentId
        val lastParentCommentId = response1?.last()?.parentCommentId

        val response2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=$lastParentCommentId&lastCommentId=$lastCommentId")
            .retrieve()
            .body<List<CommentResponse>>()

        println("secondpage")
        response2?.forEach {
            if (it.parentCommentId != it.commentId) {
                print("\t")
            }
            println(it.commentId)
        }
    }

    fun createComment(commentCreateRequest: CommentCreateRequest): CommentResponse {
        return restClient.post()
            .uri("/v1/comments")
            .body(commentCreateRequest)
            .retrieve()
            .body<CommentResponse>()!!
    }


}