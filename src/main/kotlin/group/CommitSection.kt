package group

import kotlinx.serialization.Serializable

@Serializable
data class CommitSection(val desc: String, val types: List<String>)
