package changelog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val template: Template,

    @SerialName("postprocessor")
    val postProcess: PostProcess = PostProcess()
)
