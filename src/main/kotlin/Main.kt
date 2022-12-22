import changelog.Template
import com.charleskorn.kaml.Yaml
import com.github.syari.kgit.KGit
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import org.eclipse.jgit.revwalk.RevCommit
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

private val logger = KotlinLogging.logger {}

val cwd = Path("/home/scarf/repo/Marisa")
val changelogPath = cwd / "docs" / "changelog"
val changelogFile = changelogPath / "changelog.md"
val bbcodeFile = changelogPath / "changelog.bbcode"
val versionFile = changelogPath / "version.txt"

val conventionalTitle = Regex("""\w+(\(\w+\))?!?:\s.+""")
fun RevCommit.isConventional() = conventionalTitle.matches(shortMessage)
fun RevCommit.parent(): RevCommit = parents.singleOrNull() ?: this

/** @receiver commits from latest to oldest */
fun List<ConventionalCommit>.version(criteria: VersionCriteria) = asReversed()
    .fold(SemVer()) { acc, commit ->
        val version = commit.parseVersion(criteria)
        if (version != null) logger.debug { "$acc + $version -> ${acc + version} :: $commit" }
        acc + version
    }

val templates =
    listOf(
        "markdown",
        "bbcode"
    ).associateWith { Yaml.default.decodeFromString<Template>(Path("src/main/resources/$it.yaml").readText()) }

fun List<ConventionalCommit>.toSections(sections: ChangelogSections) =
    groupBy { it.parseType(sections) }
        .filterNotNull()
        .toSortedMap(compareBy {
            if (it === sections.breaking) -1 else sections.sections.indexOf(it)
        })

fun main() {
    KGit.open(cwd.toFile()).run {
        val begin = prevId(findId("v0.0.0"))
        val commits = log { addRange(begin, HEAD()) }
            .mapNotNull { ConventionalCommit.of(it) }


        Template.builder(commits)(templates["bbcode"]!!).also(::println)

//        tag {
//            name = changelog.tag
//            message = changelog.toMarkdown()
//        }
    }
}
