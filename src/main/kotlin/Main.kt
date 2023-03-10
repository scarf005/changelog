import com.charleskorn.kaml.Yaml
import com.github.syari.kgit.KGit
import config.Config
import generator.ChangelogGenerator
import group.CommitGroup
import group.ConventionalCommit
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import org.eclipse.jgit.revwalk.RevCommit
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.io.path.writeText

val logger = KotlinLogging.logger {}

val cwd = Path("/home/scarf/repo/Marisa")
val changelogPath = cwd / "docs" / "generator"
val changelogFile = changelogPath / "changelog.md"
val bbcodeFile = changelogPath / "changelog.bbcode"
val versionFile = changelogPath / "version.txt"

val conventionalTitle = Regex("""\w+(\(\w+\))?!?:\s.+""")
fun RevCommit.isConventional() = conventionalTitle.matches(shortMessage)
fun RevCommit.parent(): RevCommit = parents.singleOrNull() ?: this

fun Path.toConfig() = Yaml.default.decodeFromString<Config>(readText())

val resources = Path("src/main/resources")
val markdown = (resources / "markdown.yaml").toConfig()
val bbcode = (resources / "bbcode.yaml").toConfig()
val sts = (resources / "slaythespire.yaml").toConfig()

val manual = Path("/home/scarf/repo/Marisa/docs/changelog")


fun main() {
    KGit.open(cwd.toFile()).run {
        val lastTag = tagList().maxBy { it.toSemVer() }
        val commits = log { addRange(peel(lastTag), HEAD()) }
            .mapNotNull { ConventionalCommit.of(it) }

        val group = CommitGroup(lastTag.toSemVer(), commits)
        val changelog = ChangelogGenerator(group)

        println(changelog.render(markdown))

        mapOf(markdown to "md", bbcode to "bbcode", sts to "sts.txt")
            .map { (k, ext) -> changelog.render(k) to (manual / "changelog.$ext") }
            .forEach { (text, file) -> file.writeText(text) }

        (manual / "version.txt").writeText(group.version.toString())

        if (lastTag.toSemVer() == group.version) return
        tag {
            name = group.version.tag
        }
    }
}
