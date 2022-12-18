fun <T, R> Iterable<T>.groupCountBy(selector: (T) -> R): Map<R, Int> =
    groupBy(selector).mapValues { it.value.size }
