package kuke.board.common.dataserializer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * JSON 직렬화/역직렬화를 담당하는 유틸리티 객체
 * - Kotlin 데이터 클래스 지원
 * - Nullable 타입 처리
 * - 기본 파라미터 값 지원
 * - ISO-8601 날짜 형식 처리
 */
object DataSerializer {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    val objectMapper: ObjectMapper = createObjectMapper()

    /**
     * 객체를 JSON 문자열로 직렬화
     * @param value 직렬화할 객체
     * @return JSON 문자열
     * @throws RuntimeException 직렬화 실패 시
     */
    inline fun <reified T : Any> serialize(value: T): String =
        runCatching {
            objectMapper.writeValueAsString(value)
        }.onFailure {
            logger.error("[serialize] Failed to serialize: value=$value, type=${T::class.java}", it)
        }.getOrThrow()

    /**
     * JSON 문자열을 객체로 역직렬화
     * @param data JSON 문자열
     * @return 역직렬화된 객체
     * @throws RuntimeException 역직렬화 실패 시
     */
    inline fun <reified T> deserialize(data: String): T =
        runCatching {
            objectMapper.readValue<T>(data)
        }.onFailure {
            logger.error("[deserialize] Failed to deserialize: data=$data, type=${T::class.java}", it)
        }.getOrThrow()

    fun <T> deserialize(data: Any, clazz: Class<T>) =
        runCatching {
            objectMapper.convertValue(data, clazz) as T

        }.onFailure {
            logger.error("[deserialize] Failed to deserialize: data=$data, type=${clazz.name}", it)
        }.getOrThrow()

    private fun createObjectMapper() = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        registerModule(kotlinModule())

        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    }
}