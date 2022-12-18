import java.io.File

val initial = "1d058628729d77644a151f6aa82b380479a1a941"

fun main() {
    println(commitsBetween(initial, cwd = File("/home/scarf/repo/Marisa")))
}
