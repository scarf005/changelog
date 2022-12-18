import java.io.File

/** stores conventional commit */
data class Commit(val sha: String, val type: String, val title: String)

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

fun latestTag(cwd: File = File(".")): String =
    "git describe --abbrev=0 --tags".runCommand(cwd).trim()
