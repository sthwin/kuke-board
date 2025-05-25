package kuke.board.view

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan("kuke.board")
@EnableJpaRepositories("kuke.board")
@SpringBootApplication
class ViewApplication

fun main(args: Array<String>) {
    runApplication<ViewApplication>(*args)
}

