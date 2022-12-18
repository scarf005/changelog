import java.io.File

/**
 * @param begin inclusive
 * @param end inclusive
 */
class CommitRange(
    val cwd: File = File("."),
    begin: String, end: String = "HEAD",
) {
    val commits = commitsBetween(begin, end, cwd)
    private val sections: String by lazy {
        commits
            .groupBy { it.changeLogType() }
            .map { (type, commits) -> changelogSection(type, commits) }
            .joinToString("\n\n")
    }
    val changelog: String by lazy {
        sections.let {
            """
            |# ${commits.toSemVer()} (${commits.last().date()})
            |
            |$it
            """.trimMargin()
        }
    }

    fun Commit.date(): String =
        "git log -1 --pretty=tformat:%cd --date=format:%Y-%m-%d $sha".runCommand(cwd).trim()
}

fun changelogSection(type: ChangeLogType, commits: List<Commit>): String =
    """
        |## ${type.desc}
        |
        |${commits.joinToString("\n") { "- ${it.title}" }}
    """.trimMargin()
