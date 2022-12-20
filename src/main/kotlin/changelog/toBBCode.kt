package changelog

import Commit

fun Changelog.toBBCode() =
    """
        |[h1]$tag ($date)[/h1]
        |
        |${sections(::section)}
        |
    """.trimMargin()

private fun section(type: ChangeLogType, commits: List<Commit>): String =
    """
        |[h2]${type.desc}[/h2]
        |
        |[list]
        |${commits.joinToString("\n") { "    [*] ${it.title.replaceCode()}" }}
        |[/list]
    """.trimMargin()

private fun String.replaceCode() =
    replace("`([^`]+)`".toRegex()) { "[code]${it.groupValues[1]}[/code]" }
