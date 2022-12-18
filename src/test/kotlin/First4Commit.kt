import changelog.Changelog
import changelog.toBBCode
import changelog.toMarkdown
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

object First4Commit : StringSpec({
    val changelog = Changelog(begin = "cac70d4", end = "98ce0b3")
    val resources = Path("src/test/resources")

    "markdown" {
        changelog.toMarkdown() shouldBe (resources / "first4.md").readText()
    }

    "bbcode" {
        changelog.toBBCode() shouldBe (resources / "first4.bbcode").readText()
    }
})
