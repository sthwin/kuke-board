package kuke.board.common.event

import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventTest {

    @Test
    fun serializeAndDeserialize() {
        // given
        val payload = ArticleCreatedEventPayload(
            articleId = 1L,
            title = "title",
            content = "content",
            boardId = 1L,
            writerId = 1L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            boardArticleCount = 23L,
        )

        val event = Event.of(
            eventId = 1234L,
            type = EventType.ARTICLE_CREATED,
            payload = payload
        )

        val json = event.toJson()
        println(json)

        // when
        val deserializedEvent = Event.fromJson<ArticleCreatedEventPayload>(json)

        // then
        assertEquals(event, deserializedEvent)
        assertEquals(event.payload, deserializedEvent.payload)
        assertThat(deserializedEvent.payload).isInstanceOf(payload::class.java)
    }
}