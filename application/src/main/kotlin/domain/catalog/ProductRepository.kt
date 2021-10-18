package domain.catalog

import arrow.core.Either

sealed class ProductRepositoryError
data class DatabaseError(val cause: Throwable): ProductRepositoryError()
data class ProductNotFound(val id: ProductId): ProductRepositoryError()

interface ProductRepository {

    fun save(product: Product): Either<ProductRepositoryError, Product>

    fun findAll(): Either<ProductRepositoryError, List<Product>>

    fun findById(id: ProductId): Either<ProductRepositoryError, Product>
}