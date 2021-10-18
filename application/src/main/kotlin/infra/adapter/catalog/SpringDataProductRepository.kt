package infra.adapter.catalog

import domain.catalog.Product
import domain.catalog.ProductId
import org.springframework.data.repository.CrudRepository

interface SpringDataProductRepository: CrudRepository<Product, ProductId> {
}