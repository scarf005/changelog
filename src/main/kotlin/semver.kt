#!/bin/env kscript


enum class Version { MAJOR, MINOR, PATCH }
data class SemVer(val major: Int, val minor: Int, val patch: Int) {
    override fun toString(): String = "$major.$minor.$patch"
}

val types = listOf("build", "ci", "docs", "feat", "fix", "perf", "refactor", "style", "test")

fun <T, R> Iterable<T>.groupCountBy(selector: (T) -> R): Map<R, Int> =
    groupBy(selector).mapValues { it.value.size }

fun version(type: String) = when {
    "!" in type -> Version.MAJOR
    type == "feat" -> Version.MINOR
    else -> Version.PATCH
}

fun List<Commit>.toSemVer() =
    groupCountBy { version(it.type) }
        .let { SemVer(it[Version.MAJOR] ?: 0, it[Version.MINOR] ?: 0, it[Version.PATCH] ?: 0) }
