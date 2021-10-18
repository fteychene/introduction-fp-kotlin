package infra

import arrow.core.traverseEither
import domain.catalog.Product
import domain.catalog.ProductRepository
import infra.adapter.catalog.SpringDataProductRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@SpringBootApplication
@EntityScan("domain.*") // Should not have to do that
class InitDbApplication

@Component
@Profile("init")
class InitDb(
    val productRepository: ProductRepository
) {

    @PostConstruct
    fun init() {
        listOf(
            Product(name = "Ten Ton Hammer", price = 9.0),
            Product(name = "Umbilical Wires", price = 8.5),
            Product(name = "Snake Pit Poetry", price = 8.0),
            Product(name = "Memories of your Ghost", price = 8.0),
            Product(name = "Bower Bird", price = 4.5),
            Product(name = "Condor", price = 4.0),
            Product(name = "Ibex", price = 4.0),
            Product(name = "Nautilus", price = 4.0),
            Product(name = "King Louie", price = 3.5),
            Product(name = "Big Viper", price = 4.3),
            Product(name = "Blue Jay", price = 5.0),
            Product(name = "Apricot Wit", price = 3.9),
            Product(name = "DIPA Cryo Hops", price = 7.0),
            Product(name = "Dark Peated", price = 5.5),
            Product(name = "Hoppy Pale Ale", price = 6.0),
            Product(name = "Russian Imperial Stout", price = 8.0),
        ).traverseEither {
            productRepository.save(it)
        }.fold(
            { e -> println("Error $e") },
            { products -> println("Inserted products: $products") }
        )
    }
}

fun main() {
    runApplication<InitDbApplication> {
        setAdditionalProfiles("init")
    }
}