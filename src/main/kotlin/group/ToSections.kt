package group

fun List<ConventionalCommit>.toSections(sections: CommitSections) =
    groupBy { it.parseType(sections) }
        .filterNotNull()
        .toSortedMap(compareBy {
            if (it === sections.breaking) -1 else sections.sections.indexOf(it)
        })
