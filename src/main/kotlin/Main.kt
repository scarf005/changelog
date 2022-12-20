import changelog.Changelog
import changelog.toBBCode
import changelog.toMarkdown
import com.github.syari.kgit.KGit
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.writeText


val initial = "1d058628729d77644a151f6aa82b380479a1a941"


val cwd = Path("/home/scarf/repo/Marisa")
val changelogPath = cwd / "docs" / "changelog"
val changelogFile = changelogPath / "changelog.md"
val bbcodeFile = changelogPath / "changelog.bbcode"
val versionFile = changelogPath / "version.txt"

fun main() {
    KGit.open(cwd.toFile()).run {
        val tags = tagList()
        if (tags.isEmpty()) {
            println("No tags found, please create a tag first")
            return
        }
        val latestTag = tags.last()

        val hash = repository.refDatabase.peel(latestTag).peeledObjectId.name
        val changelog = Changelog(
            cwd.toFile(),
            begin = hash,
            baseVersion = SemVer.from(latestTag.name)
        )

        versionFile.writeText(changelog.version.toString())
        changelogFile.writeText(changelog.toMarkdown())
        bbcodeFile.writeText(changelog.toBBCode())

        tag {
            name = changelog.tag
            message = changelog.toMarkdown()
        }
    }
}
