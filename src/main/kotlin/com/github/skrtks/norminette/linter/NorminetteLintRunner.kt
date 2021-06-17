package com.github.skrtks.norminette.linter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import settings.Option
import settings.Settings
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit


fun lint(editor: Editor?): Array<NorminetteWarning> {
    val norminettePath = Settings.get(Option.OPTION_KEY_CPPLINT)
    var pythonPath = Settings.get(Option.OPTION_KEY_PYTHON)
    var norminetteOptions = Settings.get(Option.OPTION_KEY_CPPLINT_OPTIONS)

    val tmpFile = File.createTempFile("norminette", ".c")
    val document = editor?.document ?: return emptyArray()
    createSyncedFile(document, tmpFile.toPath())
    val res = "norminette ${tmpFile.path}".runCommand(File(norminettePath))
    return parseResult(res, document)
}

private fun createSyncedFile(doc: Document, tmp: Path): VirtualFile? {
    Files.newBufferedWriter(tmp, StandardCharsets.UTF_8).use { out -> out.write(doc.text) }
    return LocalFileSystem.getInstance().findFileByPath(tmp.toString())
}

fun parseResult(res: String?, document: Document): Array<NorminetteWarning> {
    val errors = res?.split("\n")
    return errors?.mapNotNull { if (it.startsWith("Error: ")) parseError(it, document) else null }?.toTypedArray()
        ?: emptyArray()
}

fun parseError(error: String, document: Document): NorminetteWarning? {
    val blocks = error.split("\\s".toRegex()).filter { it != "" }
    val shortCode = blocks[1]
    val line = blocks[blocks.indexOf("(line:") + 1].filter { it.isDigit() }.toIntOrNull() ?: return null
    val column = blocks[blocks.indexOf("col:") + 1].filter { it.isDigit() }.toIntOrNull() ?: return null
    return NorminetteWarning(line, column, shortCode)
}


fun String.runCommand(norminette: File): String? {
    return try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}