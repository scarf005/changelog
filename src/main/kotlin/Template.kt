import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Template(
    val changelog: String, val section: String, val items: String
) {
    private fun ConventionalCommit.renderItem() = items.replace("{desc}", desc)
    private fun SortedMap<ChangelogSectionType, List<ConventionalCommit>>.renderSections() =
        map { (key, commits) ->
            val list = commits.joinToString("\n") { it.renderItem() }
            section
                .replace("{desc}", key.desc)
                .replace("{items}", list) + "\n"
        }

    fun toChangelog(
        commits: List<ConventionalCommit>,
        section: ChangelogSections,
        criteria: VersionCriteria
    ): String {
        val date = commits.first().commit.date()
        val version = commits.version(criteria)

        return commits
            .toSections(section)
            .map { (key, commits) ->
                val list = commits.joinToString("\n") {
                    it.renderItem()
                }
                markdownTemplate.section
                    .replace("{desc}", key.desc)
                    .replace("{items}", list) + "\n"
            }
            .let {
                markdownTemplate.changelog
                    .replace("{sections}", it.joinToString("\n"))
                    .replace("{tag}", version.toString())
                    .replace("{date}", date)
            }
    }

    companion object {
        fun of(
            commits: List<ConventionalCommit>,
            section: ChangelogSections,
            criteria: VersionCriteria,
        ): (Template) -> String =
            { template: Template -> template.toChangelog(commits, section, criteria) }
    }
}
