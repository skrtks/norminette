package com.github.skrtks.norminette.linter

import com.github.skrtks.norminette.settings.NorminetteSettingsPanel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import org.jetbrains.annotations.NonNls
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit


fun lint(editor: Editor?): Array<NorminetteWarning> {
    if (NorminetteSettingsPanel.NORMINETTE_PATH_VAL.isEmpty()) NorminetteSettingsPanel.detectAndSetPath()
    val norminettePath = NorminetteSettingsPanel.NORMINETTE_PATH_VAL
    if (!isValidNorminettePath(norminettePath)) return emptyArray()

    val document = editor?.document ?: return emptyArray()
    val fileExtension = FileDocumentManager.getInstance().getFile(document)?.extension ?: return emptyArray()
    val tmpFile = File.createTempFile("norminette", ".$fileExtension")
    createSyncedFile(document, tmpFile.toPath())
    val externalAnnotatorResult = "$norminettePath ${tmpFile.path}".runCommand()
    tmpFile.delete()
    return parse(externalAnnotatorResult)
}

private fun isValidNorminettePath(norminettePath: @NonNls String): Boolean {
    if (norminettePath.isEmpty()) return false
    val norminetteExecutable = File(norminettePath)
    if (!norminetteExecutable.exists() || !norminetteExecutable.canExecute()) return false
    return true
}

private fun createSyncedFile(doc: Document, tmp: Path) {
    Files.newBufferedWriter(tmp, StandardCharsets.UTF_8).use { out -> out.write(doc.text) }
}

fun parse(res: String?): Array<NorminetteWarning> {
    val errors = res?.split("\n")
    return errors?.mapNotNull { if (it.startsWith("Error: ")) parseError(it) else null }?.toTypedArray()
        ?: emptyArray()
}

fun parseError(error: String): NorminetteWarning? {
    val blocks = error.split("\\s".toRegex()).filter { it != "" }
    val shortCode = blocks[1]
    val line = blocks[blocks.indexOf("(line:") + 1].filter { it.isDigit() }.toIntOrNull() ?: return null
    return NorminetteWarning(line, shortCode)
}


fun String.runCommand(): String? {
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