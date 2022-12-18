import java.io.File


fun commitsBetween(begin: String, end: String, cwd: File) =
    listOf("git", "log", "--pretty=format:%h %s", "$begin^..$end")
        .runCommand(cwd).lines()
        .filter { title -> types.any { title.substringAfter(' ').startsWith(it) } }
        .map {
            val (sha, message) = it.split(' ', limit = 2)
            val (type, title) = message.split(':', limit = 2)
            Commit(sha, "!" in type, type.removeSuffix("!"), title.trim())
        }

fun latestTag(cwd: File): String =
    "git describe --abbrev=0 --tags".runCommand(cwd).trim()
