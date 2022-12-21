class ChangelogSections(
    vararg val sections: ChangelogSectionType,
    breakingText: String = "Breaking Changes"
) {
    val breaking = ChangelogSectionType(breakingText, emptyList())
}
