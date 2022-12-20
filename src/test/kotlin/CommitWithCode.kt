import changelog.Changelog
import changelog.toBBCode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

object CommitWithCode : StringSpec({
    val changelog = Changelog(begin = "ec0b149", end = "ec0b149")
    val resources = Path("src/test/resources") / "withCode.bbcode"

    "ec0b149" {
        changelog.toBBCode() shouldBe resources.readText()
    }
})
