package changelog

import Commit

fun Changelog.toMarkdown() =
    """
        |# $tag ($date)
        |
        |${sections(::section)}
        |
    """.trimMargin()


private fun section(type: ChangeLogType, commits: List<Commit>): String =
    """
       |## ${type.desc}
       |
       |${commits.joinToString("\n") { "- ${it.title}" }}
    """.trimMargin()