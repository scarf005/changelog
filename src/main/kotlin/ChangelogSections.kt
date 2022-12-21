import kotlinx.serialization.Serializable

@Serializable
data class ChangelogSectionType(val desc: String, val types: List<String>)

class ChangelogSections(
    vararg val sections: ChangelogSectionType,
    breakingText: String = "Breaking Changes"
) {
    val breaking = ChangelogSectionType(breakingText, emptyList())
}
