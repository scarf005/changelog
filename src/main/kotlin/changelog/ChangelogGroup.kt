package changelog

import ChangelogSections
import ConventionalCommit
import date
import toSections
import version
import version.VersionCriteria

class ChangelogGroup(
    commits: List<ConventionalCommit>,
    sectionCriteria: ChangelogSections = ChangelogSections(),
    versionCriteria: VersionCriteria = VersionCriteria(),
) {
    val date = commits.first().commit.date()
    val version = commits.version(versionCriteria)
    val logGroup = commits.toSections(sectionCriteria)
}
