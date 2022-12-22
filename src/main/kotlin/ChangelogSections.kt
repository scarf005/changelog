import kotlinx.serialization.Serializable

@Serializable
data class SectionType(val desc: String, val types: List<String>)

@Serializable
data class ChangelogSections(
    val breaking: SectionType = SectionType("Breaking Changes", emptyList()),
    val sections: List<SectionType> = listOf(
        SectionType("New Features", listOf("feat")),
        SectionType("Fixes", listOf("fix")),
    )
)
