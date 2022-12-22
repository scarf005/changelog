package changelog

import ConventionalCommit
import SectionType
import config.Config
import config.Template

fun Map<String, String>.template(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

class ChangelogGenerator(
    private val changelogGroup: ChangelogGroup
) {
    private fun applyTemplate(
        template: Template,
    ): String {
        fun renderItem(message: String) = template.items.replace("{desc}", message)

        fun renderSection(type: SectionType, list: List<ConventionalCommit>) =
            mapOf(
                "{desc}" to type.desc,
                "{items}" to list.joinToString("\n") { renderItem(it.desc) }
            ).template(template.section)

        return changelogGroup.logGroup
            .map { (type, list) -> renderSection(type, list) }
            .let {
                mapOf(
                    "{sections}" to it.joinToString("\n").trimEnd(),
                    "{tag}" to "v${changelogGroup.version}",
                    "{date}" to changelogGroup.date,
                ).template(template.changelog)
            }
    }

    fun render(config: Config): String {
        val postProcessor = PostProcessor(config.postProcess)
        val text = applyTemplate(config.template)
        return postProcessor.process(text)
    }
}
