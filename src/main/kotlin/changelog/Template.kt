package changelog

import kotlinx.serialization.Serializable

fun Map<String, String>.template(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

fun Map<Regex, String>.templateRegex(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }


/**
 * @property keys the keys to use for the changelog sections
 */
@Serializable
data class Template(
    val changelog: String,
    val section: String,
    val items: String,
    val replace: Map<String, String>? = null,
    val keys: Map<String, String>? = null,
) {
    init {
        this.validate()
    }
}
