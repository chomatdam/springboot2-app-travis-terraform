package de.upday.newsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import reactor.core.publisher.Hooks

@SpringBootApplication
@EnableScheduling
class NewsServiceApplication

fun main(args: Array<String>) {
    Hooks.onOperatorDebug()
    runApplication<NewsServiceApplication>(*args)
}
