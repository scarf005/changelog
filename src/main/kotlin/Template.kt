import kotlinx.serialization.Serializable

@Serializable
data class Template(
    val changelog: String, val section: String, val items: String
)
