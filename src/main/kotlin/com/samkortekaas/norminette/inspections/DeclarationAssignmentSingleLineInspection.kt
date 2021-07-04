package com.samkortekaas.norminette.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.psi.OCDeclarationStatement
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor
import com.jetbrains.cidr.lang.symbols.cpp.OCDeclaratorSymbol

class DeclarationAssignmentSingleLineInspection : Norminette() {
    override fun runForWholeFile(): Boolean {
        return true
    }

    override fun worksWithClangd(): Boolean {
        return true
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            private val DESCRIPTION_TEMPLATE = "Declaration and assignment on the same line"
            override fun visitDeclarationStatement(stmt: OCDeclarationStatement?) {
                val symbol = stmt?.declaration?.declarators?.first()?.symbol
                val initializer = (symbol as OCDeclaratorSymbol).initializer
                if (initializer != null && !(symbol.isGlobal || symbol.isStatic)) {
                    holder.registerProblem(stmt, DESCRIPTION_TEMPLATE, ProblemHighlightType.WEAK_WARNING)
                }
            }
        }
    }
}