import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

object Example : StringSpec({
    "hello world" {
        1 shouldBe 1
    }
})
