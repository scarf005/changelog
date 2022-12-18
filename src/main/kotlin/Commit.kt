import java.io.File

val types = listOf("build", "ci", "docs", "feat", "fix", "perf", "refactor", "style", "test")

/** stores conventional commit */
data class Commit(val sha: String, val type: String, val title: String)

fun version(type: String) = when {
    "!" in type -> Version.MAJOR
    type == "feat" -> Version.MINOR
    else -> Version.PATCH
}

/**
 * @param begin inclusive
 * @param end inclusive
 */
fun commitsBetween(begin: String, end: String = "HEAD", cwd: File = File(".")) =
    listOf("git", "log", "--pretty=format:'%h %s'", "$begin^..$end")
        .runCommand(cwd).lines()
        .filter { title -> types.any { title.substringAfter(' ').startsWith(it) } }
        .map {
            val (sha, message) = it.split(' ', limit = 2)
            val (type, title) = message.split(':', limit = 2)
            Commit(sha, type, title.trim())
        }
