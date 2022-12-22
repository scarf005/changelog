package changelog

import ChangelogSections
import ConventionalCommit
import SectionType
import config.Config
import config.Template
import date
import toSections
import version
import version.VersionCriteria

fun Map<String, String>.template(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

class ChangelogGenerator(
    private val commits: List<ConventionalCommit>,
    private val sectionCriteria: ChangelogSections = ChangelogSections(),
    private val versionCriteria: VersionCriteria = VersionCriteria(),
) {
    private fun applyTemplate(
        template: Template,
        commits: List<ConventionalCommit>
    ): String {
        val date = commits.first().commit.date()
        val version = commits.version(versionCriteria)

        fun renderItem(message: String) = template.items.replace("{desc}", message)

        fun renderSection(type: SectionType, list: List<ConventionalCommit>) =
            mapOf(
                "{desc}" to type.desc,
                "{items}" to list.joinToString("\n") { renderItem(it.desc) }
            ).template(template.section)

        return commits
            .toSections(sectionCriteria)
            .map { (type, list) -> renderSection(type, list) }
            .let {
                mapOf(
                    "{sections}" to it.joinToString("\n").trimEnd(),
                    "{tag}" to "v$version",
                    "{date}" to date,
                ).template(template.changelog)
            }
    }

    fun render(config: Config): String {
        val postProcessor = PostProcessor(config.postProcess)
        val text = applyTemplate(config.template, commits)
        return postProcessor.process(text)
    }
}
