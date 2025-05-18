package kuke.board.common.event.payload

import kuke.board.common.event.EventPayload

class ArticleViewedEventPayload(
    val articleId: Long,
    val articleViewCount: Long,
) : EventPayload {
}