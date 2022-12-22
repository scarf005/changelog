import org.eclipse.jgit.revwalk.RevCommit

class ConventionalCommit(val commit: RevCommit) {
    private val groups = regex.matchEntire(commit.shortMessage)!!.groups

    val type = groups["type"]!!.value
    val scope = groups["scope"]?.value
    val breaking = groups["breaking"] != null
    val desc = groups["description"]!!.value

    override fun toString() = commit.shortMessage!!
    override fun equals(other: Any?) = other is ConventionalCommit && commit == other.commit
    override fun hashCode() = commit.hashCode()

    fun parseVersion(criteria: VersionCriteria): Version? =
        if (breaking) Version.MAJOR else criteria.version(type)

    fun parseType(criteria: ChangelogSections): ChangelogSectionType? =
        if (breaking) criteria.breaking else criteria.sections.find { it.types.contains(type) }

    companion object {
        fun of(commit: RevCommit) =
            if (commit.isConventional()) ConventionalCommit(commit) else null

        val regex =
            Regex("""(?<type>\w+)(\((?<scope>\w+)\))?(?<breaking>!)?:\s(?<description>.+)""")
    }
}
