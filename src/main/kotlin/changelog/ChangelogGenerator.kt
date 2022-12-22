package changelog

import ChangelogSections
import ConventionalCommit
import SectionType
import VersionCriteria
import date
import toSections
import version

class ChangelogGenerator(
    private val commits: List<ConventionalCommit>,
    private val sectionCriteria: ChangelogSections = ChangelogSections(),
    private val versionCriteria: VersionCriteria = VersionCriteria(),
) {
    fun render(template: Template): String {
        fun String.applyKeys() =
            template.keys?.mapKeys { "{${it.key}}" }?.template(this) ?: this

        fun String.applyRegex() =
            template.replace?.mapKeys { it.key.toRegex() }?.templateRegex(this) ?: this

        fun renderItem(message: String) = template.items.replace("{desc}", message)
        fun renderSection(type: SectionType, commits: List<ConventionalCommit>) =
            mapOf(
                "{desc}" to type.desc,
                "{items}" to commits.joinToString("\n") { renderItem(it.desc) }
            ).template(template.section)


        val date = commits.first().commit.date()
        val version = commits.version(versionCriteria)

        return commits
            .toSections(sectionCriteria)
            .map { (type, commits) -> renderSection(type, commits) }
            .let {
                mapOf(
                    "{sections}" to it.joinToString("\n").trimEnd(),
                    "{tag}" to "v$version",
                    "{date}" to date,
                ).template(template.changelog)
            }
            .applyRegex()
            .applyKeys()
    }
}
