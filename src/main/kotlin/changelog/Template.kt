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

    private fun String.applyKeys() = keys?.let { keys ->
        keys.entries.fold(this) { acc, (k, v) ->
            acc.replace("{$k}", v)
        }
    } ?: this

    /** find regex [keys] then replace with [replace] */
    private fun String.applyRegex() = replace?.let { replace ->
        replace.entries.fold(this) { acc, (k, v) ->
            acc.replace(k.toRegex(), v)
        }
    } ?: this

    private fun ConventionalCommit.renderItem() = items.replace("{desc}", desc)
    private fun SortedMap<SectionType, List<ConventionalCommit>>.renderSections() =
        map { (key, commits) ->
            val list = commits.joinToString("\n") { it.renderItem() }
            section
                .replace("{desc}", key.desc)
                .replace("{items}", list)
        }

    private fun toChangelog(
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
                changelog
                    .replace("{sections}", it.joinToString("\n").trimEnd())
                    .replace("{tag}", "v$version")
                    .replace("{date}", date)
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

fun Template.validate() {
    fun keys(s: String) =
        Regex("""\{(\w+)}""").findAll(s).map { it.groupValues[1] }.toSet()

    fun checkReservedKeys() {
        val reservedKeys = setOf("tag", "date", "desc", "sections")
        val keys = (keys?.keys ?: emptySet()).toSet()
        val diff = reservedKeys.intersect(keys)
        require(diff.isEmpty()) { "The following keys are reserved $reservedKeys but got $diff" }
    }

    fun Set<String>.shouldContain(text: String) {
        val keys = keys(text)
        require(this.containsAll(keys)) { "keys must be a subset of $this, but ${keys - this} is not" }
    }

    fun allKeys(vararg strings: String) = strings.toSet() + (keys?.keys ?: emptySet())

    checkReservedKeys()
    allKeys("tag", "date", "sections").shouldContain(changelog)
    allKeys("desc", "items").shouldContain(section)
    allKeys("desc").shouldContain(items)
}
