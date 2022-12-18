/**
 * stores conventional commit. scope is not yet supported.
 *
 * @property sha simplified commit hash
 * @property breaking true if commit contains breaking change
 * @property type type of commit, e.g. feat, fix, refactor, etc.
 * @property title title of commit, e.g. fix typo
 */
data class Commit(
    val sha: String,
    val breaking: Boolean = false,
    val type: String,
    val title: String,
) {
    override fun toString() = "$sha $type: $title"
}
