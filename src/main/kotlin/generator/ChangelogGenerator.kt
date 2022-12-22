package generator

import config.Config
import config.Template
import group.CommitGroup
import group.CommitSection
import group.ConventionalCommit

fun Map<String, String>.template(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

class ChangelogGenerator(
    private val commitGroup: CommitGroup
) {
    private fun applyTemplate(
        template: Template,
    ): String {
        fun renderItem(message: String) = template.items.replace("{desc}", message)

        fun renderSection(type: CommitSection, list: List<ConventionalCommit>) =
            mapOf(
                "{desc}" to type.desc,
                "{items}" to list.joinToString("\n") { renderItem(it.desc) }
            ).template(template.section)

        return commitGroup.logGroup
            .map { (type, list) -> renderSection(type, list) }
            .let {
                mapOf(
                    "{sections}" to it.joinToString("\n").trimEnd(),
                    "{tag}" to commitGroup.version.tag,
                    "{date}" to commitGroup.date,
                ).template(template.changelog)
            }
    }

    fun render(config: Config): String {
        val template = config.template
        val postProcess = config.postProcess

        TemplateValidator(template, postProcess).validate()

        val text = applyTemplate(template)
        return PostProcessor(postProcess).process(text)
    }
}
