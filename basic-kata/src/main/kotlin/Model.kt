
typealias CartId = String
typealias CustomerId = String

data class Cart(var id: CartId, var customer: CustomerId, var amount: Double= 0.0)

interface Storage<T> {
    fun flush(item: T)
}