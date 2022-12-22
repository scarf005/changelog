package changelog

import kotlinx.serialization.Serializable


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
