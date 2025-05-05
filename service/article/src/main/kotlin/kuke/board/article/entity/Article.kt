package kuke.board.article.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article")
@Entity
data class Article(
    @Id
    @Column(name = "article_id")
    var articleId: Long,
    var title: String,
    var content: String,
    @Column(name = "board_id")
    var boardId: Long,  // shard key
    @Column(name = "writer_id")
    var writerId: Long,
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at")
    var modifiedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
        this.modifiedAt = LocalDateTime.now()
    }
}