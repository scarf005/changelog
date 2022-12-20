import changelog.Changelog
import changelog.toMarkdown
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.readText

object ChangelogTest : StringSpec({
    "first 4 commits (cac70d4^..98ce0b3)" {
        Changelog(
            begin = "cac70d4",
            end = "98ce0b3"
        ).toMarkdown() shouldBe Path("src/test/resources/first4.md").readText()
    }
})
