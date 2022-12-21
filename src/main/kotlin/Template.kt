import kotlinx.serialization.Serializable
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
    // TODO: assert only allowed {key}s are in template string
    init {
        allKeys("tag", "date", "sections").shouldContain(changelog)
        allKeys("desc", "items").shouldContain(section)
        allKeys("desc").shouldContain(items)
    }

    private fun Set<String>.shouldContain(text: String) = text.keys().let {
        require(this.containsAll(it)) { "keys must be a subset of $this, but ${it - this} is not" }
    }


    private fun allKeys(vararg strings: String) = strings.toSet() + (keys?.keys ?: emptySet())
    private fun String.keys() =
        Regex("""\{(\w+)}""").findAll(this).map { it.groupValues[1] }.toSet()

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
    private fun SortedMap<ChangelogSectionType, List<ConventionalCommit>>.renderSections() =
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
                    .replace("{sections}", it.joinToString("\n"))
                    .replace("{tag}", version.toString())
                    .replace("{date}", date)
            }
            .applyRegex()
            .applyKeys()
    }

    companion object {
        fun builder(
            commits: List<ConventionalCommit>,
            section: ChangelogSections,
            criteria: VersionCriteria,
        ): (Template) -> String =
            { template: Template -> template.toChangelog(commits, section, criteria) }
    }
}
