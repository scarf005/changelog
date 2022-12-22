import changelog.Template
import com.github.syari.kgit.KGit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

object First4Commit : StringSpec({
    val commits = KGit.open(File(".")).run {
        log { add(findId("98ce0b3")) }
    }.mapNotNull { ConventionalCommit.of(it) }

    val builder = Template.builder(commits)
    val resources = Path("src/test/resources")

    "markdown" {
        builder(templates["markdown"]!!) shouldBe (resources / "first4.md").readText()
    }

    "bbcode" {
        builder(templates["bbcode"]!!) shouldBe (resources / "first4.bbcode").readText()
    }
})
