package generator

import config.PostProcess
import config.Template

fun Set<String>.shouldContain(keys: Set<String>) {
    require(this.containsAll(keys)) { "keys must be a subset of $this, but ${keys - this} is not" }
}

class TemplateValidator(private val template: Template, private val postProcess: PostProcess) {
    fun keys(s: String) =
        Regex("""\{(\w+)}""").findAll(s).map { it.groupValues[1] }.toSet()

    fun checkReservedKeys() {
        val reservedKeys = setOf("tag", "date", "desc", "sections")
        val keys = postProcess.additionalKeys.keys.toSet()
        val diff = reservedKeys.intersect(keys)
        require(diff.isEmpty()) { "The following keys are reserved $reservedKeys but got $diff" }
    }

    fun allKeys(set: Set<String>) = set + postProcess.additionalKeys.keys

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
