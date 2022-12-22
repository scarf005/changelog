package changelog

import kotlinx.serialization.Serializable

@Serializable
data class Config(val template: Template, val postprocessor: PostProcessor = PostProcessor())
