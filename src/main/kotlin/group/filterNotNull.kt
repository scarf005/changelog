package group

fun <K, V> Map<K?, V?>.filterNotNull(): Map<K, V> =
    filter { (k, v) -> k != null && v != null }
        .map { (k, v) -> k!! to v!! }
        .toMap()
