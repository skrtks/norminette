package com.github.skrtks.norminette.linter

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import settings.Option
import settings.Settings
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun lint(file: PsiFile, manager: InspectionManager, document: Document): Array<ProblemDescriptor> {
    val norminettePath = Settings.get(Option.OPTION_KEY_CPPLINT)
    var pythonPath = Settings.get(Option.OPTION_KEY_PYTHON)
    var norminetteOptions = Settings.get(Option.OPTION_KEY_CPPLINT_OPTIONS)


    // setup process
    // run norm on file
    // parse output line by line
    // create problem descriptor
    // return descr
    // repeat until last line

    val localFile = file.virtualFile.path ?: return emptyArray()

    val res = "norminette $localFile".runCommand(File(norminettePath))

    return parseResult(res, manager, file)
}

fun parseResult(res: String?, manager: InspectionManager, file: PsiFile): Array<ProblemDescriptor> {
    val errors = res?.split("\n")
    return errors?.mapNotNull { if (it.startsWith("Error: ")) parseError(it, manager, file) else null }?.toTypedArray()
        ?: emptyArray()
}

fun parseError(error: String, manager: InspectionManager, file: PsiFile): ProblemDescriptor {
    val blocks = error.split("\\s".toRegex()).filter { it != "" }
    val shortCode = blocks[1]
    val line = blocks[blocks.indexOf("(line:") + 1].filter { it.isDigit() }.toInt()
    val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
    val lineStartOffset = document?.getLineStartOffset(line)
    val lineEndOffset = document?.getLineEndOffset(line)
    return manager.createProblemDescriptor(
        file,
        TextRange.create(lineStartOffset!!, lineEndOffset!!),
        shortCode,
        ProblemHighlightType.WEAK_WARNING,
        true
    )
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