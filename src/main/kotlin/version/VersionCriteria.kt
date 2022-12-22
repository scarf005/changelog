package version

import kotlinx.serialization.Serializable

@Serializable
data class VersionCriteria(
    val majors: List<String> = emptyList(),
    val minors: List<String> = listOf("feat"),
    val patches: List<String> = listOf("fix")
) {
    fun version(type: String) = when (type) {
        in majors -> Version.MAJOR
        in minors -> Version.MINOR
        in patches -> Version.PATCH
        else -> null
    }
}
