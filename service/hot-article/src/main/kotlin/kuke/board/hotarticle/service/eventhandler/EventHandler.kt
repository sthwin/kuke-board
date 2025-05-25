package kuke.board.hotarticle.service.eventhandler

import kuke.board.common.event.Event
import kuke.board.common.event.EventPayload

interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(eventType: Event<out EventPayload>): Boolean
    fun findArticleId(event: Event<out EventPayload>): Long
}