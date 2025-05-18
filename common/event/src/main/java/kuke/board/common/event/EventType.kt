package kuke.board.common.event

import kuke.board.common.event.payload.*
import kotlin.reflect.KClass

enum class EventType(
    val payloadClass: KClass<out EventPayload>, // out을 사용한 이유는 EventPayload 의 하위 타입도 허용하겠다는 의미임.
    val topic: String,
) {
    ARTICLE_CREATED(
        payloadClass = ArticleCreatedEventPayload::class,
        topic = Topic.KUKE_BOARD_ARTICLE
    ),
    ARTICLE_UPDATED(
        payloadClass = ArticleUpdatedEventPayload::class,
        topic = Topic.KUKE_BOARD_ARTICLE
    ),
    ARTICLE_DELETED(
        payloadClass = ArticleDeletedEventPayload::class,
        topic = Topic.KUKE_BOARD_ARTICLE
    ),

    COMMENT_CREATED(
        payloadClass = CommentCreatedEventPayload::class,
        topic = Topic.KUKE_BOARD_COMMENT
    ),
    COMMENT_DELETED(
        payloadClass = CommentDeletedEventPayload::class,
        topic = Topic.KUKE_BOARD_COMMENT
    ),

    ARTICLE_LIKED(
        payloadClass = ArticleLikedEventPayload::class,
        topic = Topic.KUKE_BOARD_LIKE
    ),
    ARTICLE_UNLIKED(
        payloadClass = ArticleLikedEventPayload::class,
        topic = Topic.KUKE_BOARD_LIKE
    ),
    ARTICLE_VIEWED(
        payloadClass = ArticleViewedEventPayload::class,
        topic = Topic.KUKE_BOARD_VIEW
    );

    companion object {
        fun from(topic: String): EventType = valueOf(topic)
    }

}

object Topic {
    const val KUKE_BOARD_ARTICLE = "kuke-board-article"
    const val KUKE_BOARD_COMMENT = "kuke-board-comment"
    const val KUKE_BOARD_LIKE = "kuke-board-like"
    const val KUKE_BOARD_VIEW = "kuke-board-view"
}