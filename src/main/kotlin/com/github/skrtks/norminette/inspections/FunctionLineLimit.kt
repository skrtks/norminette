package com.github.skrtks.norminette.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElementVisitor
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.jetbrains.cidr.lang.psi.OCBlockStatement
import com.jetbrains.cidr.lang.psi.OCFunctionDefinition
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class FunctionLineLimit : Norminette() {
    override fun runForWholeFile(): Boolean {
        return true
    }

    override fun worksWithClangd(): Boolean {
        return true
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            private val MAX_LINES = "A function can't have more than 25 lines"
            override fun visitFunctionDefinition(definition: OCFunctionDefinition?) {
                val file = definition?.project?.let { PsiDocumentManager.getInstance(it) }
                    ?.getDocument(definition.containingFile)
                val block = definition?.children?.filterIsInstance<OCBlockStatement>()?.first() ?: return
                val firstLine = block.startOffset.let { file?.getLineNumber(it) } ?: return
                val lastLine = block.endOffset.let { file?.getLineNumber(it) } ?: return
                if (lastLine - firstLine - 1 > 25) {
                    holder.registerProblem(definition, MAX_LINES, ProblemHighlightType.WEAK_WARNING)
                }
            }
        }
    }
}