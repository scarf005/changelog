import java.io.File

val initial = "1d058628729d77644a151f6aa82b380479a1a941"

fun main() {
    val cwd = File("/home/scarf/repo/Marisa")
    val begin = latestTag(cwd).ifEmpty { initial }
    val commits = CommitRange(cwd, begin)

    println(commits.changelog)
}

enum class ChangeLogType(val desc: String) {
    BREAKING("Breaking Changes"), FEAT("New Features"), FIXES("Fixes"), OTHER("Other Changes")
}

fun Commit.changeLogType() = when (type) {
    "feat" -> ChangeLogType.FEAT
    "fix" -> ChangeLogType.FIXES
    else -> ChangeLogType.OTHER
}
