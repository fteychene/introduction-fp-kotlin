package lottery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Lottery

fun main(args: Array<String>) {
    runApplication<Lottery>(*args)
}