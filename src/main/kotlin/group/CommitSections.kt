package group

import kotlinx.serialization.Serializable

@Serializable
data class CommitSections(
    val breaking: CommitSection = CommitSection("Breaking Changes", emptyList()),
    val sections: List<CommitSection> = listOf(
        CommitSection("New Features", listOf("feat")),
        CommitSection("Fixes", listOf("fix")),
    )
)
