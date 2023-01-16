import group.ConventionalCommit
import org.eclipse.jgit.lib.Ref
import version.SemVer
import version.VersionCriteria

/** @receiver commits from latest to oldest */
fun List<ConventionalCommit>.version(begin: SemVer, criteria: VersionCriteria) = asReversed()
    .fold(begin) { acc, commit ->
        val version = commit.parseVersion(criteria)
        if (version != null) logger.debug { "$acc + $version -> ${acc + version} :: $commit" }
        acc + version
    }

fun Ref.toSemVer() = name.substringAfterLast('v').split(".").map { it.toInt() }
    .let { (major, minor, patch) -> SemVer(major, minor, patch) }
