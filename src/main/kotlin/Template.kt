import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Template(
    val changelog: String,
    val section: String,
    val items: String,
    val replace: Map<String, String>? = null
) {
// TODO: assert only allowed {key}s are in template string

    private fun ConventionalCommit.renderItem() = items.replace("{desc}", desc)
    private fun SortedMap<ChangelogSectionType, List<ConventionalCommit>>.renderSections() =
        map { (key, commits) ->
            val list = commits.joinToString("\n") { it.renderItem() }
            section
                .replace("{desc}", key.desc)
                .replace("{items}", list)
        }

    fun toChangelog(
        commits: List<ConventionalCommit>,
        changelogSections: ChangelogSections,
        criteria: VersionCriteria
    ): String {
        val date = commits.first().commit.date()
        val version = commits.version(criteria)

        return commits
            .toSections(changelogSections)
            .map { (key, commits) ->
                val list = commits.joinToString("\n") {
                    it.renderItem()
                }
                section
                    .replace("{desc}", key.desc)
                    .replace("{items}", list)
            }
            .let {
                changelog
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
