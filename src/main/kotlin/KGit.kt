import com.github.syari.kgit.KGit
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private fun String.isConventional() = conventionalTitle.matches(this)

/** @return peeled commit id of tag */
private fun KGit.findTagId(tag: String) =
    tagList().find { it.name.endsWith(tag) }?.let { peel(it) }

fun KGit.peel(ref: Ref): ObjectId = repository.refDatabase.peel(ref).peeledObjectId ?: ref.objectId
fun KGit.findId(s: String): ObjectId =
    findTagId(s) ?: repository.resolve(s) ?: throw IllegalArgumentException("Cannot resolve $s")

@Suppress("FunctionName")
fun KGit.HEAD() = repository.resolve("HEAD")!!
fun KGit.prevId(id: ObjectId): ObjectId = repository.parseCommit(id).parent().toObjectId()
fun KGit.logBetween(from: ObjectId, to: ObjectId = HEAD()) = log { addRange(from, to) }


fun RevCommit.date(): String =
    Date(commitTime * 1000L)
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
