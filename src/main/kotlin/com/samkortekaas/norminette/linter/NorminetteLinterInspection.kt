package com.samkortekaas.norminette.linter

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.samkortekaas.norminette.fixes.TestFix
import java.util.function.Consumer

class NorminetteLinterInspection : ExternalAnnotator<Editor, List<NorminetteWarning>>() {
    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): Editor {
        return editor
    }

    override fun doAnnotate(editor: Editor?): List<NorminetteWarning> {
        return NorminetteLintRunner.lint(editor).toList()
    }

    override fun apply(file: PsiFile, warnings: List<NorminetteWarning>, holder: AnnotationHolder) {
        val document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return
        warnings.forEach(Consumer forEach@{ warning: NorminetteWarning ->
            val line: Int = warning.line - 1
            val startOffset = document.getLineStartOffset(line)
            val endOffset = document.getLineEndOffset(line)

            if (!isProperRange(startOffset, endOffset)) {
                return@forEach
            }
            val range = TextRange(startOffset, endOffset)

//            holder.createWeakWarningAnnotation(range, warning.reason)

            val element = file.findElementAt(range.startOffset)

            if (element != null) {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, warning.reason)
                    .range(range)
                    .highlightType(ProblemHighlightType.WEAK_WARNING)
                    .withFix(TestFix(element))
                    .create();
            } else {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, warning.reason)
                    .range(range)
                    .highlightType(ProblemHighlightType.WEAK_WARNING)
                    .create()
            }
        })
    }

    private fun isProperRange(startOffset: Int, endOffset: Int): Boolean {
        return startOffset in 0..endOffset
    }
}

