package kuke.board.comment.service

import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import kuke.board.comment.service.request.CommentCreateRequest
import kuke.board.comment.service.response.CommentPageResponse
import kuke.board.comment.service.response.CommentResponse
import kuke.board.common.dataserializer.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    val commentRepository: CommentRepository
) {
    val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequest): CommentResponse {
        val parent = findParent(request)
        val comment = commentRepository.save(
            Comment.create(
                commentId = snowflake.nextId(),
                content = request.content,
                parentCommentId = parent?.commentId,
                articleId = request.articleId,
                writerId = request.writerId
            )
        )

        return comment.toCommentResponse()
    }

    private fun findParent(request: CommentCreateRequest): Comment? {
        return request.parentCommentId?.let { parentCommentId ->
            commentRepository.findById(parentCommentId)
                .filter { it.deleted.not() }
                .filter { it.root }
                .orElseThrow()
        }
    }

    fun read(commentId: Long): CommentResponse {
        return commentRepository.findById(commentId)
            .orElseThrow()
            .toCommentResponse()
    }

    @Transactional
    fun delete(commentId: Long) {
        commentRepository.findById(commentId)
            .filter { it.deleted.not() }
            .ifPresent { comment ->
                if (hasChildren(comment)) {
                    comment.delete()
                } else {
                    delete(comment)
                }
            }
    }

    private fun hasChildren(comment: Comment): Boolean {
        return commentRepository.countBy(
            articleId = comment.articleId,
            parentCommentId = comment.commentId,
            limit = 2
        ) == 2L
    }

    /**
     * 전달받은 코맨트가 루트가 아니면, 그 코맨트의 부모 코맨트들을 찾는다.
     * 삭제표시가 되고, 자식이 없는 부모 코맨트가 있으면 삭제 한다.
     *
     */
    private fun delete(comment: Comment) {
        commentRepository.delete(comment)
        if (comment.root.not()) {
            commentRepository.findById(comment.parentCommentId)
                .filter { it.deleted }
                .filter { hasChildren(it).not() }
                .ifPresent { delete(it) }
        }
    }

    fun readAll(
        articleId: Long,
        page: Long,
        pageSize: Long
    ): CommentPageResponse {
        return CommentPageResponse(
            comments = commentRepository.findAll(
                articleId = articleId,
                offset = (page - 1) * pageSize,
                limit = pageSize
            ).map { it.toCommentResponse() },
            commentCount = commentRepository.count(
                articleId = articleId,
                limit = PageLimitCalculator.calculatePageLimit(
                    page = page,
                    pageSize = pageSize,
                    movablePageCount = 10
                )
            )
        )
    }

    fun readAll(
        articleId: Long,
        lastParentCommentId: Long?,
        lastCommentId: Long?,
        pageSize: Long
    ): List<CommentResponse> {
        return if (lastParentCommentId == null || lastCommentId == null) {
            commentRepository.findAllInfiniteScroll(
                articleId = articleId,
                limit = pageSize
            )
        } else {
            commentRepository.findAllInfiniteScroll(
                articleId = articleId,
                lastParentCommentId = lastParentCommentId,
                lastCommentId = lastCommentId,
                limit = pageSize
            )
        }.map {
            it.toCommentResponse()
        }
    }
}