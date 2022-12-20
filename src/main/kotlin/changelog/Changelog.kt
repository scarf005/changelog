package changelog

import Commit
import SemVer
import commitsBetween
import latestTag
import root
import runCommand
import toSemVer
import java.io.File

/**
 * @param begin inclusive
 * @param end inclusive
 */
class Changelog(
    val cwd: File = File("."),
    begin: String = latestTag(cwd).ifEmpty { root(cwd) },
    end: String = "HEAD",
    baseVersion: SemVer = SemVer(),
) {
    /** first -> latest commit, last -> oldest commit */
    val commits = commitsBetween(begin, end, cwd).ifEmpty { error("No commits found") }
        .filter { it.isIncluded() }

    val version = commits.toSemVer(baseVersion)
    val tag = "v${this.version}"
    val date = commits.first().date()

    fun sections(section: (ChangeLogType, List<Commit>) -> String, height: Int = 2) = commits
        .groupByTo(sortedMapOf()) { it.changeLogType() }
        .map { (type, commits) -> section(type, commits) }
        .joinToString("\n" * height)

    private fun Commit.isIncluded() = breaking || type in listOf("feat", "fix")
    private fun Commit.date(): String =
        "git log -1 --pretty=tformat:%cd --date=format:%Y-%m-%d $sha".runCommand(cwd).trim()

    fun Commit.changeLogType() = when (type) {
        "feat" -> ChangeLogType.FEAT
        "fix" -> ChangeLogType.FIXES
        else -> ChangeLogType.BREAKING
    }
}

private operator fun String.times(size: Int) = repeat(size)
