
// Function could be returned

fun inMemoryStorage(values: List<String>): (String) -> List<String> =
    { searched -> values.filter { it.contains(searched.lowercase()) }}


inMemoryStorage(listOf("beer", "wine", "water", "soda"))("a")

// Function as value

val repository = inMemoryStorage(listOf("beer", "wine", "water", "soda"))

repository("w")

// Function could be received

fun search(searching: String, f: (String) -> List<String>): String {
    return "Result for ${searching} is ${f(searching)}"
}

search("W", repository)
search("B", repository)