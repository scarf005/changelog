package generator

fun Map<Regex, String>.templateRegex(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }
