import changelog.Changelog
import changelog.toBBCode
import changelog.toMarkdown
import com.github.syari.kgit.KGit
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.writeText


val initial = "1d058628729d77644a151f6aa82b380479a1a941"


fun main() {
    val cwd = Path("/home/scarf/repo/Marisa")
    val changelog = Changelog(cwd.toFile(), begin = initial)
    val changelogPath = cwd / "docs" / "changelog"
    val changelogFile = changelogPath / "changelog.md"
    val bbcodeFile = changelogPath / "changelog.bbcode"
    val versionFile = changelogPath / "version.txt"

    versionFile.writeText(changelog.version)
    changelogFile.writeText(changelog.toMarkdown())
    bbcodeFile.writeText(changelog.toBBCode())

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
