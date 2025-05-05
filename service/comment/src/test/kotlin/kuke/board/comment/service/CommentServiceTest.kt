package kuke.board.comment.service

import kuke.board.comment.entity.Comment
import kuke.board.comment.repository.CommentRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class CommentServiceTest(

) {
    @InjectMocks
    lateinit var commentService: CommentService

    @Mock
    lateinit var commentRepository: CommentRepository

    @Test
    @DisplayName("삭제할 댓글이 자식을 가지고 있으면 삭제 표시만 한다.")
    fun deleteShouldMarkDeletedIfHasChildren() {
        // given
        val articleId = 1L
        val commentId = 2L

        val comment = createComment(articleId, commentId)
        given(commentRepository.findById(commentId)).willReturn(
            Optional.of(comment)
        )
        given(
            commentRepository.countBy(
                articleId = articleId,
                parentCommentId = commentId,
                limit = 2L
            )
        ).willReturn(2L)

        // when
        commentService.delete(commentId)

        // then
        verify(comment).delete()
    }

    @Test
    @DisplayName("리프 코맨트가 삭제될 때 이 코맨트의 부모가 있고, 이 부모가 삭제되지 않았다면 리프 코맨트만 삭제함.")
    fun deleteShouldDeleteChildOnlyIfNotDeletedParent() {
        // given
        val articleId = 1L
        val commentId = 2L
        val parentCommentId = 1L

        val comment = createComment(articleId, commentId, parentCommentId)
        given(comment.root).willReturn(false)

        val parentComment = mock<Comment>()
        given(parentComment.deleted).willReturn(false)

        given(commentRepository.findById(commentId)).willReturn(
            Optional.of(comment)
        )

        // 자식이 없음
        given(
            commentRepository.countBy(
                articleId = articleId,
                parentCommentId = commentId,
                limit = 2L
            )
        ).willReturn(1L)

        given(commentRepository.findById(parentCommentId)).willReturn(
            Optional.of(parentComment)
        )

        // when
        commentService.delete(commentId)

        // then
        verify(commentRepository).delete(comment)
        verify(commentRepository, never()).delete(parentComment)
    }

    @Test
    @DisplayName(
        "리프 코맨트가 삭제될 때, 이 코맨트의 부모가 있고, 이 부모가 삭제된 상태면, " +
                "부모 코맨트에 대해서도 재귀적으로 delete 메서드를 호출해서 삭제한다."
    )
    fun deleteShouldDeleteAllRecursivelyIfDeletedParent() {
        // given
        val articleId = 1L
        val commentId = 2L
        val parentCommentId = 1L

        val comment = createComment(articleId, commentId, parentCommentId)
        given(comment.root).willReturn(false)

        val parentComment = createComment(articleId, parentCommentId)
        given(parentComment.root).willReturn(true)
        given(parentComment.deleted).willReturn(true)

        given(commentRepository.findById(commentId)).willReturn(
            Optional.of(comment)
        )
        given(
            commentRepository.countBy(
                articleId = articleId,
                parentCommentId = commentId,
                limit = 2L
            )
        ).willReturn(1L)

        given(commentRepository.findById(parentCommentId)).willReturn(
            Optional.of(parentComment)
        )
        given(
            commentRepository.countBy(
                articleId = articleId,
                parentCommentId = parentCommentId,
                limit = 2L
            )
        ).willReturn(1L)

        // when
        commentService.delete(commentId)

        // then
        verify(commentRepository).delete(comment)
        verify(commentRepository).delete(parentComment)
    }

    fun createComment(
        articleId: Long,
        commentId: Long
    ): Comment {
        return mock<Comment>().also {
            given(it.articleId).willReturn(articleId)
            given(it.commentId).willReturn(commentId)
        }
    }

    fun createComment(
        articleId: Long,
        commentId: Long,
        parentCommentId: Long
    ): Comment {
        return createComment(articleId, commentId)
            .also {
                given(it.parentCommentId).willReturn(parentCommentId)
            }
    }

}