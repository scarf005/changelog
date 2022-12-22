import changelog.ChangelogGenerator
import changelog.ChangelogGroup
import com.github.syari.kgit.KGit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

object CommitWithCode : StringSpec({
    val commit = KGit.open(File(".")).run {
        repository.parseCommit(repository.resolve("ec0b149"))
    }.let(ConventionalCommit.Companion::of)


    val resources = Path("src/test/resources") / "withCode.bbcode"

    "ec0b149" {
        ChangelogGenerator(ChangelogGroup(listOf(commit!!))).render(bbcode) shouldBe resources.readText()
    }
})
