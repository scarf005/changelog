package changelog

fun Template.validate() {
    fun keys(s: String) =
        Regex("""\{(\w+)}""").findAll(s).map { it.groupValues[1] }.toSet()

    fun checkReservedKeys() {
        val reservedKeys = setOf("tag", "date", "desc", "sections")
        val keys = (keys?.keys ?: emptySet()).toSet()
        val diff = reservedKeys.intersect(keys)
        require(diff.isEmpty()) { "The following keys are reserved $reservedKeys but got $diff" }
    }

    fun Set<String>.shouldContain(text: String) {
        val keys = keys(text)
        require(this.containsAll(keys)) { "keys must be a subset of $this, but ${keys - this} is not" }
    }

    fun allKeys(vararg strings: String) = strings.toSet() + (keys?.keys ?: emptySet())

    checkReservedKeys()
    allKeys("tag", "date", "sections").shouldContain(changelog)
    allKeys("desc", "items").shouldContain(section)
    allKeys("desc").shouldContain(items)
}
