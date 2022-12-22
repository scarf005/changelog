package group

import date
import version
import version.VersionCriteria

class CommitGroup(
    commits: List<ConventionalCommit>,
    sectionCriteria: CommitSections = CommitSections(),
    versionCriteria: VersionCriteria = VersionCriteria(),
) {
    val date = commits.first().commit.date()
    val version = commits.version(versionCriteria)
    val logGroup = commits.toSections(sectionCriteria)
}
