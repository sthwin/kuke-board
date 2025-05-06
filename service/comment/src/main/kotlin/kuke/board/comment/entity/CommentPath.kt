package kuke.board.comment.entity

import jakarta.persistence.Embeddable


/**
 * path는 62진수인 5자리 청크 문자들로 구성됩니다.
 * 뎁스 표현방식은 아래와 같습니다. 예제 뎁스의 공백은 가독성을 위해 넣었습니다. 실제 값에는 공백이 없습니다.
 * ex) 1뎁스:00000, 2뎁스: 00000 00000, 3뎁스: 00000 00000 00000
 *
 */
@Embeddable
data class CommentPath(
    val path: String
) {

    companion object {
        const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

        const val DEPTH_CHUNK_SIZE = 5
        const val MAX_DEPTH = 5

        // MIN_CHUNK = "00000", MAX_CHUNK = "zzzzz"
        val MIN_CHUNK = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        val MAX_CHUNK = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        fun of(path: String): CommentPath {
            if (isDepthOverFlowed(path)) {
                throw IllegalStateException("depth over flowed")
            }
            return CommentPath(path)
        }

        private fun isDepthOverFlowed(path: String) = calcDepth(path) > MAX_DEPTH

        private fun isChunkOverFlowed(chunk: String) = MAX_CHUNK == chunk

        private fun calcDepth(path: String) = path.length / DEPTH_CHUNK_SIZE
    }

    val depth: Int
        get() = calcDepth(path)

    val root: Boolean
        get() = depth == 1

    val parentPath: String
        get() = path.dropLast(DEPTH_CHUNK_SIZE)

    fun createChildCommentPath(descendantsTopPath: String?): CommentPath {
        if (descendantsTopPath == null) {
            return of(path + MIN_CHUNK)
        }
        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return CommentPath(increase(childrenTopPath))
    }

    private fun findChildrenTopPath(descendantsTopPath: String): String {
        return descendantsTopPath.substring(0, (depth + 1) * DEPTH_CHUNK_SIZE)
    }

    private fun increase(path: String): String {
        val lastChunk = path.takeLast(DEPTH_CHUNK_SIZE)
        if (isChunkOverFlowed(lastChunk)) {
            throw IllegalStateException("chunk over flowed")
        }

        val charsetLength = CHARSET.length

        // 62진수를 10진수로 변환
        var value = 0
        for (ch in lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(ch)
        }

        value = value + 1

        // 10진수를 62진수로 변환
        val sb = StringBuilder(DEPTH_CHUNK_SIZE)
        var remainingValue = value

        repeat(DEPTH_CHUNK_SIZE) {
            sb.insert(0, CHARSET[remainingValue % charsetLength])
            remainingValue /= charsetLength
        }

        return path.substring(0, path.length - DEPTH_CHUNK_SIZE) + sb.toString()
    }

}
