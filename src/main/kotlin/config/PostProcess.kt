package config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property regexKeys regex to transform text
 * @property additionalKeys additional keys to used as template variable
 */
@Serializable
data class PostProcess(
    @SerialName("replace")
    val regexKeys: Map<String, String> = emptyMap(),

    @SerialName("keys")
    val additionalKeys: Map<String, String> = emptyMap(),
)
