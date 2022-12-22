package changelog

import ChangelogSections
import ConventionalCommit
import SectionType
import VersionCriteria
import date
import kotlinx.serialization.Serializable
import toSections
import version
import java.util.*

fun Map<String, String>.template(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }

fun Map<Regex, String>.templateRegex(text: String) =
    entries.fold(text) { acc, (k, v) -> acc.replace(k, v) }


/**
 * @property keys the keys to use for the changelog sections
 */
@Serializable
data class Template(
    val changelog: String,
    val section: String,
    val items: String,
    val replace: Map<String, String>? = null,
    val keys: Map<String, String>? = null,
) {
    init {
        this.validate()
    }

    private fun String.applyKeys() = keys?.mapKeys { "{${it.key}}" }?.template(this) ?: this


    /** find regex [keys] then replace with [replace] */
    private fun String.applyRegex() =
        replace?.mapKeys { it.key.toRegex() }?.templateRegex(this) ?: this

    private fun ConventionalCommit.renderItem() = items.replace("{desc}", desc)
    private fun SortedMap<SectionType, List<ConventionalCommit>>.renderSections() =
        map { (key, commits) ->
            val list = commits.joinToString("\n") { it.renderItem() }

            mapOf("{desc}" to key.desc, "{items}" to list).template(section)
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
            .renderSections()
            .let {
                mapOf(
                    "{sections}" to it.joinToString("\n").trimEnd(),
                    "{tag}" to "v$version",
                    "{date}" to date,
                ).template(changelog)
            }
            .applyRegex()
            .applyKeys()
    }

    companion object {
        fun builder(
            commits: List<ConventionalCommit>,
            section: ChangelogSections = ChangelogSections(),
            criteria: VersionCriteria = VersionCriteria(),
        ): (Template) -> String =
            { template: Template -> template.toChangelog(commits, section, criteria) }
    }
}
