package changelog

import Commit

fun Changelog.toBBCode(repoURL: String = "scarf005/Marisa") =
    """
        |[h1]$tag ($date)[/h1]
        |
        |${sections(::section, height = 1).replacePR(repoURL)}
        |
    """.trimMargin()

private fun section(type: ChangeLogType, commits: List<Commit>): String =
    """
        |[h2]${type.desc}[/h2]
        |[list]
        |${commits.joinToString("\n") { "    [*] ${it.title.replaceCode()}" }}
        |[/list]
    """.trimMargin()

private fun String.replaceCode() =
    replace("`([^`]+)`".toRegex()) { "[i]${it.groupValues[1]}[/i]" }

private fun String.replacePR(repoURL: String) =
    replace("#(\\d+)".toRegex()) { "[url=https://github.com/${repoURL}/pull/${it.groupValues[1]}]${it.groupValues[0]}[/url]" }
