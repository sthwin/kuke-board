package kuke.board.comment.service

import kuke.board.comment.entity.ArticleCommentCount
import kuke.board.comment.entity.CommentPath
import kuke.board.comment.entity.CommentV2
import kuke.board.comment.repository.ArticleCommentCountRepository
import kuke.board.comment.repository.CommentRepositoryV2
import kuke.board.comment.service.request.CommentCreateRequestV2
import kuke.board.comment.service.response.CommentPageResponseV2
import kuke.board.comment.service.response.CommentResponseV2
import kuke.board.common.dataserializer.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class CommentServiceV2(
    val commentRepositoryV2: CommentRepositoryV2,
    val articleCommentCountRepository: ArticleCommentCountRepository,
) {
    val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponseV2 {
        val parent = findParent(request)
        val parentCommentPath = parent?.commentPath ?: CommentPath.of("")
        val comment = commentRepositoryV2.save(
            CommentV2.of(
                commentId = snowflake.nextId(),
                content = request.content,
                articleId = request.articleId,
                writerId = request.writerId,
                commentPath = parentCommentPath.createChildCommentPath(
                    descendantsTopPath = commentRepositoryV2.findDescendantsTopPath(
                        articleId = request.articleId,
                        pathPrefix = parentCommentPath.path
                    )
                )
            )
        ).toCommentResponseV2()

        val result = articleCommentCountRepository.increase(request.articleId)
        if (result == 0) {
            articleCommentCountRepository.save(
                ArticleCommentCount.init(
                    articleId = request.articleId,
                    commentCount = 1L
                )
            )
        }

        return comment
    }

    private fun findParent(request: CommentCreateRequestV2): CommentV2? {
        if (request.parentPath == null) {
            return null
        }
        return commentRepositoryV2.findByPath(request.parentPath)
            .filter { it.deleted.not() }
            .orElseThrow()
    }

    fun read(commentId: Long): CommentResponseV2 {
        return commentRepositoryV2.findById(commentId)
            .orElseThrow()
            .toCommentResponseV2()
    }

    @Transactional
    fun delete(commentId: Long) {
        commentRepositoryV2.findById(commentId)
            .filter { it.deleted.not() }
            .ifPresent { comment ->
                if (hasChildren(comment)) {
                    comment.delete()
                } else {
                    delete(comment)
                }
            }
    }

    private fun hasChildren(comment: CommentV2): Boolean {
        return commentRepositoryV2.findDescendantsTopPath(
            articleId = comment.articleId,
            pathPrefix = comment.commentPath.path
        ) != null
    }

    private fun delete(comment: CommentV2) {
        commentRepositoryV2.delete(comment)
        articleCommentCountRepository.decrease(comment.articleId)
        if (comment.root.not()) {
            commentRepositoryV2.findByPath(comment.commentPath.parentPath)
                .filter { it.deleted }
                .filter { hasChildren(it).not() }
                .ifPresent { delete(it) }
        }
    }

    fun readAll(
        articleId: Long,
        page: Long,
        pageSize: Long,
    ): CommentPageResponseV2 {
        return CommentPageResponseV2(
            comments = commentRepositoryV2.findAll(
                articleId = articleId,
                offset = (page - 1) * pageSize,
                limit = pageSize
            ).map { it.toCommentResponseV2() },
            commentCount = commentRepositoryV2.count(
                articleId = articleId,
                limit = PageLimitCalculator.calculatePageLimit(page, pageSize, 10)
            )
        )
    }

    fun readAllInfiniteScroll(
        articleId: Long,
        lastPath: String?,
        pageSize: Long,
    ): List<CommentResponseV2> {
        return if (lastPath == null) {
            commentRepositoryV2.findAllInfiniteScroll(
                articleId = articleId,
                limit = pageSize
            )
        } else {
            commentRepositoryV2.findAllInfiniteScroll(
                articleId = articleId,
                lastPath = lastPath,
                limit = pageSize
            )
        }.map { it.toCommentResponseV2() }
    }

    fun count(articleId: Long): Long {
        return articleCommentCountRepository.findById(articleId)
            .getOrNull()?.commentCount ?: 0
    }
}


