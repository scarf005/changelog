import java.io.File

fun List<String>.runCommand(workingDir: File = File(".")) =
    ProcessBuilder(this)
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream.bufferedReader().readText()

fun String.runCommand(workingDir: File = File(".")) =
    split("\\s".toRegex()).runCommand(workingDir)
