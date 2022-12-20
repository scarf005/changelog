import changelog.Changelog
import changelog.toMarkdown
import com.github.syari.kgit.KGit
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.writeText


val initial = "1d058628729d77644a151f6aa82b380479a1a941"


fun main() {
    val cwd = Path("/home/scarf/repo/Marisa")
    val changelog = Changelog(cwd.toFile(), begin = initial)
    val changelogFile = cwd / "changelog.md"
    val versionFile = cwd / "version.txt"

    versionFile.writeText(changelog.version)
    changelogFile.writeText(changelog.toMarkdown())

    changelog.toMarkdown().also(::println)

    KGit.open(cwd.toFile()).run {
        val tags = tagList()
        println(tags)
        if (tags.isNotEmpty()) return

        tag {
            name = changelog.tag
            message = changelog.toMarkdown()
        }
    }
}

fun <T> List<T>.mapIf(condition: (T) -> Boolean, transform: (T) -> T): List<T> =
    map { if (condition(it)) transform(it) else it }
