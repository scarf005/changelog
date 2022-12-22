package changelog

import kotlinx.serialization.Serializable

/**
 * @property replace regex to transform text
 * @property keys additional keys to used as template variable
 */
@Serializable
data class PostProcess(
    val replace: Map<String, String> = emptyMap(),
    val keys: Map<String, String> = emptyMap(),
)
