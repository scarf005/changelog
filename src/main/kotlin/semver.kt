enum class Version { MAJOR, MINOR, PATCH }
data class SemVer(val major: Int = 0, val minor: Int = 0, val patch: Int = 0) {
    override fun toString(): String = "$major.$minor.$patch"
    operator fun plus(other: Version?) = when (other) {
        Version.MAJOR -> copy(major = major + 1, minor = 0, patch = 0)
        Version.MINOR -> copy(minor = minor + 1, patch = 0)
        Version.PATCH -> copy(patch = patch + 1)
        null -> this
    }

    companion object {
        fun from(s: String) = s
            .replace("refs/tags/", "")
            .replace("v", "")
            .split(".")
            .map { it.toInt() }
            .let { SemVer(it[0], it[1], it[2]) }
    }
}

val types = listOf("build", "ci", "docs", "feat", "fix", "perf", "refactor", "style", "test")


fun Commit.version() = when {
    breaking -> Version.MAJOR
    type == "feat" -> Version.MINOR
    else -> Version.PATCH
}

fun createSemVer(commits: List<Commit>, baseVersion: SemVer): SemVer =
    commits.fold(baseVersion) { acc, commit -> acc + commit.version() }

fun List<Commit>.toSemVer(baseVersion: SemVer) = createSemVer(this, baseVersion)

//fun RevCommit.version() = when {
//    breaking -> Version.MAJOR
//    type == "feat" -> Version.MINOR
//    else -> Version.PATCH
//}
//fun List<RevCommit>.toSemver(base: SemVer) = fold(base) { acc, commit -> acc + commit.version() }}
