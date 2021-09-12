package com.samkortekaas.norminette.linter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.util.function.Consumer

class NorminetteAnnotator : ExternalAnnotator<Document, List<NorminetteWarning>>() {
    override fun collectInformation(file: PsiFile): Document? {
        return FileDocumentManager.getInstance().getDocument(file.virtualFile)
    }

    override fun doAnnotate(document: Document): List<NorminetteWarning> {
        return NorminetteLintRunner.lint(document).toList()
    }

    override fun apply(file: PsiFile, warnings: List<NorminetteWarning>, holder: AnnotationHolder) {
        val document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return
        warnings.forEach(Consumer forEach@{ warning: NorminetteWarning ->
            val line: Int = warning.line - 1
            val lineStartOffset = document.getLineStartOffset(line)
            val lineEndOffset = document.getLineEndOffset(line)
            val textInLine = document.getText(TextRange(lineStartOffset, lineEndOffset))
            val correctedCol = if (warning.col > 0) warning.col - 1 else warning.col
//            val tabSize = CodeStyle.getIndentOptions(file).TAB_SIZE // Norminette v3 always assumes tabs as 4 spaces, so not needed atm

            var col = 0
            var offset = 0
            while (col < correctedCol && offset < textInLine.length) {
                if (textInLine[offset] == '\t') {
                    col += (4 - col % 4)
                } else {
                    col++
                }
                offset++
            }

            val startOffset = lineStartOffset + offset
            val endOffset = startOffset + 1

            if (!isProperRange(startOffset, endOffset)) {
                return@forEach
            }
            val range = TextRange(startOffset, endOffset)
            print("Error $range col $col\n")
            holder.createAnnotation(HighlightSeverity.WEAK_WARNING, range, "Norminette: ${warning.reason}")
        })
    }

    private fun isProperRange(startOffset: Int, endOffset: Int): Boolean {
        return startOffset in 0..endOffset
    }
}
