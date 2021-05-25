package norminette.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.descendantsOfType
import com.jetbrains.cidr.lang.psi.*
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class DeclarationWhiteSpaceInspection : Norminette() {
    override fun runForWholeFile(): Boolean {
        return true
    }

    override fun worksWithClangd(): Boolean {
        return true
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            private val ONE_EMPTY_LINE = "Declarations should be separated by one empty line"
            private val DECL_TOP_FUNCT = "Declarations should be on the top of the body"
            private val EMPTY_LINE = "Empty line between declarations"
            private val EMPTY_LINE_AFTER = "There should be an empty line after the declarations block"

            override fun visitOCFile(file: OCFile?) {
                val declarations = file?.children?.filterIsInstance<OCDeclaration>()
                declarations?.forEach { decl ->
                    if (decl.prevSibling is PsiWhiteSpace) {
                        var newLineEncountered = 0
                        decl.prevSibling.text.forEach { chr ->
                            if (chr == '\n') {
                                newLineEncountered += 1
                            }
                        }
                        if (newLineEncountered != 2) {
                            holder.registerProblem(decl, ONE_EMPTY_LINE, ProblemHighlightType.WEAK_WARNING)
                        }
                    }
                }
            }

            override fun visitBlockStatement(stmt: OCBlockStatement?) {
                // Check empty line around declarations
                val declarations = stmt?.children?.filterIsInstance<OCDeclarationStatement>() ?: return
                if (declarations.isEmpty()) return
                if (declarations.first().prevSibling?.prevSibling?.text != "{") {
                    declarations.first()
                        .let { holder.registerProblem(it, DECL_TOP_FUNCT, ProblemHighlightType.WEAK_WARNING) }
                }
                declarations.forEach { decl ->
                    if (decl.prevSibling is PsiWhiteSpace) {
                        var newLineEncountered = 0
                        (decl.prevSibling as PsiWhiteSpace).text.forEach { chr ->
                            if (chr == '\n') {
                                newLineEncountered += 1
                            }
                        }
                        if (newLineEncountered != 1) {
                            holder.registerProblem(decl, EMPTY_LINE, ProblemHighlightType.WEAK_WARNING)
                        }
                    }
                }
                if (declarations.last().nextSibling?.nextSibling?.text != "}" && declarations.last().nextSibling?.text?.contains(
                        "\n\n"
                    ) != true
                ) {
                    holder.registerProblem(declarations.last(), EMPTY_LINE_AFTER, ProblemHighlightType.WEAK_WARNING)
                }
            }

            override fun visitFunctionDefinition(stmt: OCFunctionDefinition?) {
                if (stmt?.prevSibling == null || (stmt.prevSibling is PsiWhiteSpace && stmt.prevSibling.text.last() != '\n')) {
                    if (stmt != null) {
                        holder.registerProblem(
                            stmt,
                            "Whitespace before declaration is not allowed",
                            ProblemHighlightType.WEAK_WARNING
                        )
                    }
                }
                val whiteSpaceBetweenTypeAndName = stmt?.firstChild?.nextSibling
                if (whiteSpaceBetweenTypeAndName is PsiWhiteSpace) {
                    whiteSpaceBetweenTypeAndName.text?.forEach {
                        if (it != '\t') {
                            holder.registerProblem(
                                stmt,
                                "Only tabs allowed between type and name",
                                ProblemHighlightType.WEAK_WARNING
                            )
                        }
                    }
                }
            }

            override fun visitDeclaration(stmt: OCDeclaration?) {
                val whiteSpaceBetweenTypeAndName =
                    stmt?.children?.filterIsInstance<OCTypeElement>()?.first()?.nextSibling
                if (whiteSpaceBetweenTypeAndName is PsiWhiteSpace) {
                    whiteSpaceBetweenTypeAndName.text?.forEach {
                        if (it != '\t') {
                            holder.registerProblem(
                                stmt,
                                "Only tabs allowed between type and name",
                                ProblemHighlightType.WEAK_WARNING
                            )
                        }
                    }
                }
            }
        }
    }
}