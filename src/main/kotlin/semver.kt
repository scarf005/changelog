enum class Version { MAJOR, MINOR, PATCH }
data class SemVer(val major: Int, val minor: Int, val patch: Int) {
    override fun toString(): String = "$major.$minor.$patch"
}

val types = listOf("build", "ci", "docs", "feat", "fix", "perf", "refactor", "style", "test")


fun Commit.version() = when {
    breaking -> Version.MAJOR
    type == "feat" -> Version.MINOR
    else -> Version.PATCH
}

fun createSemVer(commits: List<Commit>): SemVer =
    commits.groupCountBy(Commit::version)
        .let { SemVer(it[Version.MAJOR] ?: 0, it[Version.MINOR] ?: 0, it[Version.PATCH] ?: 0) }

fun List<Commit>.toSemVer() = createSemVer(this)
