import com.github.syari.kgit.KGit
import mu.KotlinLogging
import org.eclipse.jgit.lib.ObjectId
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
private fun String.isConventional() = conventionalTitle.matches(this)

fun RevCommit.parent(): RevCommit = parents.singleOrNull() ?: this


/** @return peeled commit id of tag */
private fun KGit.findTagId(tag: String) =
    tagList().find { it.name.endsWith(tag) }?.let { repository.refDatabase.peel(it).peeledObjectId }

fun KGit.findId(s: String): ObjectId =
    findTagId(s) ?: repository.resolve(s) ?: throw IllegalArgumentException("Cannot resolve $s")

@Suppress("FunctionName")
fun KGit.HEAD() = repository.resolve("HEAD")!!

fun KGit.prevId(id: ObjectId): ObjectId = repository.parseCommit(id).parent().toObjectId()
fun KGit.logBetween(from: ObjectId, to: ObjectId = HEAD()) = log { addRange(from, to) }

class ConventionalCommit(val commit: RevCommit) {
    private val groups = regex.matchEntire(commit.shortMessage)!!.groups

    val type = groups["type"]!!.value
    val scope = groups["scope"]?.value
    val breaking = groups["breaking"] != null
    val description = groups["description"]!!.value

    override fun toString() = commit.shortMessage!!
    override fun equals(other: Any?) = other is ConventionalCommit && commit == other.commit
    override fun hashCode() = commit.hashCode()

    companion object {
        val regex =
            Regex("""(?<type>\w+)(\((?<scope>\w+)\))?(?<breaking>!)?:\s(?<description>.+)""")
    }
}

data class VersionCriteria(
    val majors: List<String> = emptyList(),
    val minors: List<String> = listOf("feat"),
    val patches: List<String> = listOf("fix")
) {
    fun version(type: String) = when (type) {
        in majors -> Version.MAJOR
        in minors -> Version.MINOR
        in patches -> Version.PATCH
        else -> null
    }
}

fun ConventionalCommit.parseVersion(criteria: VersionCriteria): Version? =
    if (breaking) Version.MAJOR else criteria.version(type)


fun main() {

    KGit.open(cwd.toFile()).run {
        val begin = prevId(findId("v0.0.0"))
        val commits = log { addRange(begin, HEAD()) }
            .filter(RevCommit::isConventional)
            .map(::ConventionalCommit)

        val criteria = VersionCriteria()
        val version = commits.asReversed()
            .fold(SemVer()) { acc, commit ->
                val version = commit.parseVersion(criteria)
                if (version != null) logger.debug { "$acc + $version -> ${acc + version} :: $commit" }
                acc + version
            }

        println(version)

        // print commit title
//        println(latest.shortMessage)

        // get commits between first and latest
//        val commits = log { initial.peeledObjectId to latest.id }.reversed()
        // print commit title
//        commits.forEach { println(it.shortMessage) }

        // get commit from hash initial

        // find tag v0.0.0 from tags
//        println(repository.resolve("v0.0.0"))
//        println(repository.resolve("HEAD"))
//        println(tagList())
//        val foo = repository.resolve("v0.0.0")
//
//        println("Latest commit: ${latest.id}")
//        println("First commit: ${first.id}")

//        val tags = tagList()
//        if (tags.isEmpty()) {
//            println("No tags found, please create a tag first")
//            return
//        }
//        val latestTag = tags.last()
//
//        val hash = repository.refDatabase.peel(latestTag).peeledObjectId.name
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
