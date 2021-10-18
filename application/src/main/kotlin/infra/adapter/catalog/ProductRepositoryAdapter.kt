package infra.adapter.catalog

import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import domain.catalog.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductRepositoryAdapter(val springDataProductRepository: SpringDataProductRepository) : ProductRepository {
    override fun save(product: Product): Either<ProductRepositoryError, Product> =
        Either.catch { springDataProductRepository.save(product) }
            .mapLeft { DatabaseError(it) }

    override fun findAll(): Either<ProductRepositoryError, List<Product>> =
        Either.catch { springDataProductRepository.findAll().toList() }
            .mapLeft { DatabaseError(it) }

    override fun findById(id: ProductId): Either<ProductRepositoryError, Product> =
        Either.catch { springDataProductRepository.findByIdOrNull(id) }
            .mapLeft { DatabaseError(it) }
            .flatMap { Option.fromNullable(it).toEither { ProductNotFound(id) } }

}