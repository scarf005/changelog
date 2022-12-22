package changelog

import config.PostProcess

class PostProcessor(private val postProcess: PostProcess) {
    private fun String.applyKeys() =
        postProcess.keys.mapKeys { "{${it.key}}" }.template(this)

    private fun String.applyRegex() =
        postProcess.replace.mapKeys { it.key.toRegex() }.templateRegex(this)

    fun process(text: String) = text.applyKeys().applyRegex()
}
