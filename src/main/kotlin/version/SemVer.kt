package version

data class SemVer(val major: Int = 0, val minor: Int = 0, val patch: Int = 0) :
    Comparable<SemVer> {
    val tag = "v$this"

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: SemVer): Int =
        compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch })

    override fun toString(): String = "$major.$minor.$patch"
    operator fun plus(other: Version?) = when (other) {
        Version.MAJOR -> copy(major = major + 1, minor = 0, patch = 0)
        Version.MINOR -> copy(minor = minor + 1, patch = 0)
        Version.PATCH -> copy(patch = patch + 1)
        null -> this
    }
}
