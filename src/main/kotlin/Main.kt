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

/** @receiver commits from latest to oldest */
fun List<ConventionalCommit>.version(criteria: VersionCriteria) = asReversed()
    .fold(SemVer()) { acc, commit ->
        val version = commit.parseVersion(criteria)
        if (version != null) logger.debug { "$acc + $version -> ${acc + version} :: $commit" }
        acc + version
    }

val changelog = """
      |# {tag} ({date})
      |
      |{sections}
""".trimMargin()
val section = """
    |## {desc}
    |
    |{items}
""".trimMargin()
val items = "- {desc}"


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
        val version = commits.version(criteria)

        val changelog =
//            log { addRange(lastTag, HEAD()) }
            log { addRange(findId("v0.0.0"), HEAD()) }
                .mapNotNull { ConventionalCommit.of(it) }
                .groupBy { it.parseType(defaultSections) }
                .filterNotNull()
                .toSortedMap(compareBy {
                    if (it === defaultSections.breaking) -1 else defaultSections.sections.indexOf(it)
                })
                .map { (key, commits) ->
                    val list = commits.joinToString("\n") { items.replace("{desc}", it.desc) }
                    section
                        .replace("{desc}", key.desc)
                        .replace("{items}", list) + "\n"
                }
                .let {
                    val date = repository.parseCommit(HEAD()).date()

                    changelog
                        .replace("{sections}", it.joinToString("\n"))
                        .replace("{tag}", version.toString())
                        .replace("{date}", date)
                }
                .also(::println)


//        tag {
//            name = changelog.tag
//            message = changelog.toMarkdown()
//        }
    }
}
