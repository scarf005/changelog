import group.ConventionalCommit
import org.eclipse.jgit.lib.Ref
import version.SemVer
import version.VersionCriteria

/**
 * @receiver range of commits.
 * @return [SemVer] increased by biggest Version of .
 */
fun List<ConventionalCommit>.version(begin: SemVer, criteria: VersionCriteria) : SemVer =
    mapNotNull { it.parseVersion(criteria) }
        .maxOf { it }
        .let { begin + it }



fun Ref.toSemVer() = name.substringAfterLast('v').split(".").map { it.toInt() }
    .let { (major, minor, patch) -> SemVer(major, minor, patch) }
