import com.github.syari.kgit.KGit
import generator.ChangelogGenerator
import group.CommitGroup
import group.ConventionalCommit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import version.SemVer
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

object First4Commit : StringSpec({
    val commits = KGit.open(File(".")).run {
        log { add(findId("98ce0b3")) }
    }.mapNotNull { ConventionalCommit.of(it) }

    val builder = ChangelogGenerator(CommitGroup(SemVer(), commits))
    val resources = Path("src/test/resources")

    "markdown" {
        builder.render(markdown) shouldBe (resources / "first4.md").readText()
    }

    "bbcode" {
        builder.render(bbcode) shouldBe (resources / "first4.bbcode").readText()
    }
})
