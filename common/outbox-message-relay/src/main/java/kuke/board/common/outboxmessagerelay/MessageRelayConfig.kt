package kuke.board.common.outboxmessagerelay

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


/**
 * @EnableAsync: 스프링에서 @Async 애노테이션이 붙은 메서드를 찾아서 비동기 실행을 가능하게 해주는 애노테이션.
 * @Configuration: 스프링 설정 클래스임을 나타내는 애노테이션.
 * @ComponentScan: 스프링에서 특정 패키지 내부의 컴포넌트(Bean)들과 그 하위 컴포넌트들도 찾아내어 Bean으로 등록하는 애노테이션.
 * @EnableScheduling: 스프링에서 @Scheduled 애노테이션이 붙은 메서드를 찾아서 스케줄링을 가능하게 해주는 애노테이션.
 */
@EnableAsync
@Configuration
@ComponentScan("kuke.board.common.outboxmessagerelay")
@EnableScheduling
class MessageRelayConfig(
    @Value("\${spring.data.kafka.bootstrap-servers}")
    private val bootstrapServers: String
) {

    @Bean
    fun messageRelayKafkaTemplate(): KafkaTemplate<String, String> {
        val configProps = mutableMapOf<String, Any>().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            put(ProducerConfig.ACKS_CONFIG, "all")
        }

        return KafkaTemplate(DefaultKafkaProducerFactory(configProps))
    }

    /**
     * 트랜잭션이 끝날때마다 비동기로 이벤트 전송을 할 때 사용됨.
     */
    @Bean
    fun messageRelayPublishEventExecutor(): ThreadPoolTaskExecutor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 20
            maxPoolSize = 50
            queueCapacity = 100
            setThreadNamePrefix("mr-pub-event-")
        }
    }

    /**
     * 이벤트 전송에 실패한 것들을 다시 전송할 때 사용됨.
     */
    @Bean
    fun messageReplayPublishPendingEventExecutor(): ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()

}