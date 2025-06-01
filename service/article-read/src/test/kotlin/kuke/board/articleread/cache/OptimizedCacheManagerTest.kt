package kuke.board.articleread.cache

import kuke.board.common.dataserializer.DataSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import kotlin.test.Test


@ExtendWith(MockitoExtension::class)
class OptimizedCacheManagerTest {
    @InjectMocks
    var optimizedCacheManager: OptimizedCacheManager? = null

    @Mock
    var stringRedisTemplate: StringRedisTemplate? = null

    @Mock
    var optimizedCacheLockProvider: OptimizedCacheLockProvider? = null

    @Mock
    var valueOperations: ValueOperations<String, String> = mock()

    @BeforeEach
    fun beforeEach() {
        given(stringRedisTemplate!!.opsForValue()).willReturn(valueOperations)
    }

    @Test
    @DisplayName("캐시 데이터가 없으면 원본 데이터 요청")
    @Throws(Throwable::class)
    fun processShouldCallOriginDataIfCachedDataIsNull() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args: Array<Any> = arrayOf(1, "param")
        val returnType: Class<String> = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val cachedData: String? = null
        given(valueOperations.get("testType::1::param")).willReturn(cachedData)

        // when
        val result: Any = optimizedCacheManager!!.process(
            type = type,
            ttlSeconds = ttlSeconds,
            args = args,
            returnType = returnType,
            originDataSupplier = originDataSupplier
        )

        // then
        assertThat(result).isEqualTo(originDataSupplier.get())
        verify(valueOperations).set(
            eq("testType::1::param"),
            anyString(), any(Duration::class.java)
        )
    }

    @Test
    @DisplayName("유효하지 않은 캐시 데이터라면 원본 데이터 요청")
    @Throws(Throwable::class)
    fun processShouldCallOriginDataIfInvalidCachedData() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args: Array<Any> = arrayOf(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val cachedData = "{::invalid" // 이렇게 받으면 예외가 발생하도록 만들었기 때문에 이렇게 테스트가 싶패함.
        given(valueOperations.get("testType::1::param")).willReturn(cachedData)

        // when
        val result: Any =
            optimizedCacheManager!!.process(
                type = type,
                ttlSeconds = ttlSeconds,
                args = args,
                returnType = returnType,
                originDataSupplier = originDataSupplier
            )

        // then
        assertThat(result).isEqualTo(originDataSupplier.get())
        verify(valueOperations).set(
            eq("testType::1::param"),
            anyString(), any(Duration::class.java)
        )
    }

    @Test
    @DisplayName("논리적으로 만료되지 않은 데이터면 캐시 데이터 반환")
    @Throws(Throwable::class)
    fun processShouldReturnCachedDataIfNotExpiredLogically() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args: Array<Any> = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val optimizedCache: OptimizedCache? = OptimizedCache.of("cached", Duration.ofSeconds(ttlSeconds))
        val cachedData: String? = DataSerializer.serialize(optimizedCache!!)
        given(valueOperations.get("testType::1::param")).willReturn(cachedData)

        // when
        val result: Any =
            optimizedCacheManager!!.process(
                type = type,
                ttlSeconds = ttlSeconds,
                args = args,
                returnType = returnType,
                originDataSupplier = originDataSupplier
            )

        // then
        assertThat(result).isEqualTo("cached")
        verify(valueOperations, never()).set(eq("testType::1::param"), anyString(), any(Duration::class.java))
    }

    @Test
    @DisplayName("논리적으로 만료된 데이터면 락 획득 시도. 락 실패 시 캐시 데이터 반환")
    @Throws(Throwable::class)
    fun processShouldReturnCachedDataIfExpiredLogicallyAndLockNotAcquired() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args: Array<Any> = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val optimizedCache: OptimizedCache? = OptimizedCache.of("cached", Duration.ofSeconds(-1))
        val cachedData: String? = DataSerializer.serialize(optimizedCache!!)
        given(valueOperations.get("testType::1::param")).willReturn(cachedData)

        given(optimizedCacheLockProvider!!.lock("testType::1::param")).willReturn(false)

        // when
        val result: Any =
            optimizedCacheManager!!.process(
                type = type,
                ttlSeconds = ttlSeconds,
                args = args,
                returnType = returnType,
                originDataSupplier = originDataSupplier
            )

        // then
        assertThat(result).isEqualTo("cached")
        verify(valueOperations, never()).set(
            eq("testType::1::param"),
            anyString(), any(Duration::class.java)
        )
    }

    @Test
    @DisplayName("논리적으로 만료된 데이터면 락 획득 시도. 락 성공 시 캐시 리프레시 후 반환")
    @Throws(Throwable::class)
    fun processShouldCallOriginDataAndRefreshCacheIfExpiredLogicallyAndLockAcquired() {
        // given
        val type = "testType"
        val ttlSeconds: Long = 10
        val args: Array<Any> = arrayOf<Any>(1, "param")
        val returnType = String::class.java
        val originDataSupplier = OptimizedCacheOriginDataSupplier { "origin" }

        val optimizedCache: OptimizedCache? = OptimizedCache.of("cached", Duration.ofSeconds(-1))
        val cachedData: String? = DataSerializer.serialize(optimizedCache!!)
        given(valueOperations.get("testType::1::param")).willReturn(cachedData)

        given(optimizedCacheLockProvider!!.lock("testType::1::param")).willReturn(true)

        // when
        val result: Any =
            optimizedCacheManager!!.process(
                type = type,
                ttlSeconds = ttlSeconds,
                args = args,
                returnType = returnType,
                originDataSupplier = originDataSupplier
            )

        // then
        assertThat(result).isEqualTo("origin")
        verify(valueOperations).set(
            eq("testType::1::param"),
            anyString(), any(Duration::class.java)
        )
    }
}