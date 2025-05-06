package kuke.board.comment.entity

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CommentPathTest {

    @Test
    fun createChildCommentTest() {
        // 00000 <- 생성
        createChildCommentTest(
            commentPath = CommentPath.of(""),
            descendantsTopPath = null,
            "00000"
        )

        // 00000
        //      00000 <-- 생성
        createChildCommentTest(
            commentPath = CommentPath.of("00000"),
            descendantsTopPath = null,
            "0000000000"
        )

        // 00000
        // 00001 <-- 생성
        createChildCommentTest(
            commentPath = CommentPath.of(""),
            descendantsTopPath = "00000",
            "00001"
        )

        // 0000z
        //      abcdz
        //           zzzzz
        //                zzzzz
        //      abce0 <-- 생성
        createChildCommentTest(
            commentPath = CommentPath.of("0000z"),
            descendantsTopPath = "0000zabcdzzzzzzzzzzz",
            expectedChildPath = "0000zabce0"
        )


    }

    fun createChildCommentTest(commentPath: CommentPath, descendantsTopPath: String?, expectedChildPath: String) {
        val childCommentPath = commentPath.createChildCommentPath(descendantsTopPath)
        assertThat(childCommentPath.path).isEqualTo(expectedChildPath)
    }

    @Test
    fun createChildCommentPathIfMaxDepthTest() {
        assertThatThrownBy {
            CommentPath.of("zzzzz".repeat(5)).createChildCommentPath(null)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun createChildCommentPathIfChunkOverflowTest() {

        // given
        val commentPath = CommentPath.of("")

        // when then
        assertThatThrownBy {
            commentPath.createChildCommentPath("zzzzz")
        }.isInstanceOf(IllegalStateException::class.java)
    }

}