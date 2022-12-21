import com.github.syari.kgit.KGit
import mu.KotlinLogging
import org.eclipse.jgit.revwalk.RevCommit
import kotlin.io.path.Path
import kotlin.io.path.div

private val logger = KotlinLogging.logger {}

val cwd = Path("/home/scarf/repo/Marisa")
val changelogPath = cwd / "docs" / "changelog"
val changelogFile = changelogPath / "changelog.md"
val bbcodeFile = changelogPath / "changelog.bbcode"
val versionFile = changelogPath / "version.txt"

val conventionalTitle = Regex("""\w+(\(\w+\))?!?:\s.+""")
fun RevCommit.isConventional() = conventionalTitle.matches(shortMessage)
fun RevCommit.parent(): RevCommit = parents.singleOrNull() ?: this

fun main() {
    val defaultSections = ChangelogSections(
        ChangelogSectionType("New Features", listOf("feat")),
        ChangelogSectionType("Fixes", listOf("fix")),
    )

    KGit.open(cwd.toFile()).run {
        val begin = prevId(findId("v0.0.0"))
        val commits = log { addRange(begin, HEAD()) }
            .mapNotNull { ConventionalCommit.of(it) }

        val criteria = VersionCriteria()
        val version = commits.asReversed()
            .fold(SemVer()) { acc, commit ->
                val version = commit.parseVersion(criteria)
                if (version != null) logger.debug { "$acc + $version -> ${acc + version} :: $commit" }
                acc + version
            }
        val lastTag = peel(tagList().last())
        val changelog =
//            log { addRange(lastTag, HEAD()) }
            log { addRange(findId("1cee4ce"), HEAD()) }
                .mapNotNull { ConventionalCommit.of(it) }
                .groupBy { it.parseType(defaultSections) }
                .onEach { println(it) }
//            .onEach { logger.info { it } }

//        val changelog = Changelog(
//            cwd.toFile(),
//            begin = hash,
//            baseVersion = SemVer.from(latestTag.name)
//        )
//
//        versionFile.writeText(changelog.version.toString())
//        changelogFile.writeText(changelog.toMarkdown())
//        bbcodeFile.writeText(changelog.toBBCode())
//
//        tag {
//            name = changelog.tag
//            message = changelog.toMarkdown()
//        }
    }
}
