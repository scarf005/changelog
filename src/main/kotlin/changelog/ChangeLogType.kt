package changelog

enum class ChangeLogType(val desc: String) {
    BREAKING("Breaking Changes"), FEAT("New Features"), FIXES("Fixes")
}
