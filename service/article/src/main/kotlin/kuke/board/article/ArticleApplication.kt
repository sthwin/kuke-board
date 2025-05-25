package kuke.board.article

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan("kuke.board")
@EnableJpaRepositories("kuke.board")
@SpringBootApplication
class ArticleApplication

fun main(args: Array<String>) {
    runApplication<ArticleApplication>(*args)
}

