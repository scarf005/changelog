package changelog

class TemplateValidator(val template: Template, val postProcess: PostProcessor) {
    fun keys(s: String) =
        Regex("""\{(\w+)}""").findAll(s).map { it.groupValues[1] }.toSet()

    fun checkReservedKeys() {
        val reservedKeys = setOf("tag", "date", "desc", "sections")
        val keys = postProcess.keys.keys.toSet()
        val diff = reservedKeys.intersect(keys)
        require(diff.isEmpty()) { "The following keys are reserved $reservedKeys but got $diff" }
    }

    fun Set<String>.shouldContain(keys: Set<String>) {
        require(this.containsAll(keys)) { "keys must be a subset of $this, but ${keys - this} is not" }
    }

    fun allKeys(set: Set<String>) = set + postProcess.keys.keys

    fun validate() {
        checkReservedKeys()
        mapOf(
            setOf("tag", "date", "sections") to template.changelog,
            setOf("desc", "items") to template.section,
            setOf("desc") to template.items,
        ).forEach { (keys, text) ->
            allKeys(keys).shouldContain(keys(text))
        }
    }
}
