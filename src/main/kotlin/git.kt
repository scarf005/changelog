import java.io.File


private fun range(begin: String, end: String, cwd: File) = when {
    begin.isEmpty() -> end
    begin.sha(cwd) == root(cwd) -> end
    else -> "$begin..$end"
}
//    .also { println("begin:${begin.sha(cwd)}, root:${root(cwd)}, ${begin.sha(cwd) == root(cwd)}") }

fun commitsBetween(begin: String, end: String, cwd: File) =
    listOf("git", "log", "--pretty=format:%h %s", range(begin, end, cwd))
        .runCommand(cwd).lines()
        .filter { title -> types.any { title.substringAfter(' ').startsWith(it) } }
        .map {
            val (sha, message) = it.split(' ', limit = 2)
            val (type, title) = message.split(':', limit = 2)
            Commit(sha, "!" in type, type.removeSuffix("!"), title.trim())
        }

fun latestTag(cwd: File): String = try {
    "git describe --abbrev=0 --tags".parse(cwd)
} catch (e: Exception) {
    ""
}

fun root(cwd: File): String = "git rev-list --all --max-parents=0".parse(cwd)

fun String.sha(cwd: File) = "git rev-parse $this".parse(cwd)

private fun String.parse(cwd: File) =
    runCommand(cwd).trim().ifEmpty { error("Failed to resolve $this") }.trim()
