









fun doSomething(list: List<String>): Map<String, Int> {
    var result = hashMapOf<String, Int>()
    for(string in list) {
        val element = result[string]
        if (element != null) {
            result[string] = element + 1
        } else {
            result[string] = 1
        }
    }
    return result
}














fun alsoDoSomething(list: List<String>): Map<String, Int> =
    list.groupBy { it }
        .map { (key, elements) -> key to elements.count() }
        .toMap()



