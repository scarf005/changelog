package changelog

import ChangelogSections
import ConventionalCommit
import SectionType
import date
import toSections
import version
import version.VersionCriteria

fun Map<String, String>.template(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

fun Map<Regex, String>.templateRegex(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

class ChangelogGenerator(
    private val commits: List<ConventionalCommit>,
    private val sectionCriteria: ChangelogSections = ChangelogSections(),
    private val versionCriteria: VersionCriteria = VersionCriteria(),
) {
    fun render(config: Config): String {
        fun String.applyKeys() =
            config.postprocessor.keys.mapKeys { "{${it.key}}" }.template(this)

        fun String.applyRegex() =
            config.postprocessor.replace.mapKeys { it.key.toRegex() }.templateRegex(this)

        fun renderItem(message: String) = config.template.items.replace("{desc}", message)
        fun renderSection(type: SectionType, commits: List<ConventionalCommit>) =
            mapOf(
                "{desc}" to type.desc,
                "{items}" to commits.joinToString("\n") { renderItem(it.desc) }
            ).template(config.template.section)


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
                ).template(config.template.changelog)
            }
            .applyRegex()
            .applyKeys()
    }
}
