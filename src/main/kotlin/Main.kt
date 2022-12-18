import java.io.File

val initial = "1d058628729d77644a151f6aa82b380479a1a941"

fun main() {
    val cwd = File("/home/scarf/repo/Marisa")
    if (latestTag(cwd).isEmpty()) {
        println("No tags found")
    }
    val commits = commitsBetween(initial, cwd = cwd)
    println(commits.toSemVer())
    println(commits.toChangelog())
}

enum class ChangeType { BREAKING, FEATURE, BUGFIX, OTHER }

fun Commit.changeLogType() = when {
    "!" in type -> ChangeType.BREAKING
    type == "feat" -> ChangeType.FEATURE
    type == "fix" -> ChangeType.BUGFIX
    else -> ChangeType.OTHER
}

fun List<Commit>.toChangelog() = groupBy { it.changeLogType() }

/*
{BUGFIX=[
    Commit(sha='617f6db, type=fix, title=shoot the moon removing transient's debuffs'),
    Commit(sha='4b0b163, type=fix, title=match `rootProject.name` (#54)'),
    Commit(sha='f154371, type=fix, title=match `rootProject.name` with modid (#52)'),
    Commit(sha='acdcb06, type=fix, title=event abort always evaluating to true (#39)'),
    Commit(sha='93120e7, type=fix, title=use class ID over raw string for `hasPower`, `hasRelic` key (#33)'),
    Commit(sha='61321d3, type=fix, title=more nullables (#31)'), Commit(sha='85de963, type=fix, title=null check on `canUse` (#20)'),
    Commit(sha='6e72e84, type=fix, title=mark nullable parameters in `use` as unused (#17)')],

OTHER=[
    Commit(sha='dde2c32, type=build, title=make dev jar name more distinguishable'),
    Commit(sha='7b25c26, type=build, title=workshop fixes (#57)'),
    Commit(sha='14d1245, type=build, title=rename to `MarisaContinued` (#51)'),
    Commit(sha='dbe72ab, type=docs, title=move previous docs into `docs/legacy` (#48)'),
    Commit(sha='ce2422c, type=docs, title=update `README` (#43)'),
    Commit(sha='ae57143, type=refactor, title=un-nest marisa.cards.Marisa (#41)'),
    Commit(sha='fcda3bc, type=refactor, title=convert remaining patches to kotlin (#37)'),
    Commit(sha='c2de7bd, type=refactor, title=use filter (#27)'),
    Commit(sha='2f9f45a, type=refactor, title=use expression body (#22)'),
    Commit(sha='0bb9ca8, type=refactor, title=marisamod (#18)'),
    Commit(sha='2c86321, type=refactor, title=migrate to kotlin, part 2 (#13)'),
    Commit(sha='5282137, type=refactor, title=migrate `ThMod.java` to kotlin (#11)'),
    Commit(sha='7f21313, type=build, title=use parallel job (#4)'), Commit(sha='1d05862, type=build, title=migrate to intellij (#1)')], FEATURE=[Commit(sha='0f627e7, type=feat, title=steam workshop release (#55)'), Commit(sha='15af5be, type=feat, title=preview generated cards (#45)'), Commit(sha='e284cae, type=feat, title=version using ISO timestamp (#16)'), Commit(sha='9646ba6, type=feat, title=apply korean fan-made i18n patches (#6)')], BREAKING=[Commit(sha='0201122, type=refactor!, title=migrate to kotlin (#5)')]}

 */
