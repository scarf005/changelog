package changelog

import Commit

fun Changelog.toMarkdown() =
    """
        |# $tag ($date)
        |
        |${sections()}
        |
    """.trimMargin()


private fun Changelog.sections() = commits
    .groupByTo(sortedMapOf()) { it.changeLogType() }
    .map { (type, commits) -> type.section(commits) }
    .joinToString("\n\n")

private fun ChangeLogType.section(commits: List<Commit>): String =
    """
       |## $desc
       |
       |${commits.joinToString("\n") { "- ${it.title}" }}
    """.trimMargin()
