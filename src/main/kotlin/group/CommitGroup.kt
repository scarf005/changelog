package group

import date
import version
import version.SemVer
import version.VersionCriteria

class CommitGroup(
    begin: SemVer,
    commits: List<ConventionalCommit>,
    sectionCriteria: CommitSections = CommitSections(),
    versionCriteria: VersionCriteria = VersionCriteria(),
) {
    val date = commits.first().commit.date()
    val version = commits.version(begin, versionCriteria)
    val logGroup = commits.toSections(sectionCriteria)
}
