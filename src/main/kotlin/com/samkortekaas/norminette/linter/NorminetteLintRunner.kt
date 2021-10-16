package com.samkortekaas.norminette.linter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.samkortekaas.norminette.MyBundle
import com.samkortekaas.norminette.settings.NorminetteSettingsPanel
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.TimeUnit

object NorminetteLintRunner {

    fun lint(document: Document): Array<NorminetteWarning> {
        if (NorminetteSettingsPanel.NORMINETTE_PATH_VAL.isEmpty()) NorminetteSettingsPanel.detectAndSetPath()
        val norminettePath = NorminetteSettingsPanel.NORMINETTE_PATH_VAL
        if (!isValidNorminettePath(norminettePath)) return emptyArray()

        val file = FileDocumentManager.getInstance().getFile(document) ?: return emptyArray()
        val tmpDir = Files.createTempDirectory("norminette")
        val tmpFile = File("$tmpDir/${file.name}")
        tmpFile.writeText(document.text)
        tmpFile.deleteOnExit()
        val externalAnnotatorResult = "$norminettePath ${tmpFile.toPath()}".runCommand()
        tmpFile.delete()
        return parse(externalAnnotatorResult)
    }

    private fun isValidNorminettePath(norminettePath: String): Boolean {
        if (norminettePath.isEmpty()) return false
        val norminetteExecutable = File(norminettePath)
        return norminetteExecutable.exists() || !norminetteExecutable.canExecute()
    }

    private fun parse(res: String?): Array<NorminetteWarning> {
        val errors = res?.split("\n")
        return errors?.mapNotNull { if (it.startsWith("Error: ")) parseError(it) else null }?.toTypedArray()
            ?: emptyArray()
    }

    private fun parseError(error: String): NorminetteWarning? {
        val blocks = error.split("\\s".toRegex()).filter { it != "" }
        val shortCode = blocks[1]
        val line = blocks[blocks.indexOf("(line:") + 1].filter { it.isDigit() }.toIntOrNull() ?: return null
        val col = blocks[blocks.indexOf("col:") + 1].filter { it.isDigit() }.toIntOrNull() ?: return null
        return if (MyBundle.containsKey(shortCode)) {
            NorminetteWarning(line, col, MyBundle.message(shortCode))
        }
        else {
            null
        }
    }


    private fun String.runCommand(): String? {
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
}