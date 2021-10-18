package infra.web

import domain.catalog.Product
import domain.catalog.ProductRepository
import infra.adapter.catalog.SpringDataProductRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController(val repository: ProductRepository) {

    @GetMapping("/")
    fun getAll(): ResponseEntity<List<Product>> =
        repository.findAll()
            .fold(
                { e ->
                    println("Error $e")
                    ResponseEntity.internalServerError().build()
                },
                { ResponseEntity.ok(it) }
            )

}