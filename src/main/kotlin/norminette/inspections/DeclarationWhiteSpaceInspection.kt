package norminette.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
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
            private val NEWLINE_AFTER_BRACE = "Expected newline after brace"
            private val DECL_TOP_FUNCT = "Declarations should be on the top of the body"
            private val EMPTY_LINE = "Empty line between declarations"
            private val EMPTY_LINE_AFTER = "There should be an empty line after the declarations block"
            private val SPACE_SHOULD_BE_TAB = "Only tabs allowed between type and name"
            private val WS_BEFORE_DECL = "Whitespace before declaration is not allowed"
            private val EMPTY_LINE_ON_TOP_OF_FILE = "Empty line on top of file"

            override fun visitOCFile(file: OCFile?) {
                val declarations = file?.children?.filterIsInstance<OCDeclaration>()
                if (file?.firstChild is PsiWhiteSpace && file.firstChild.text.first() == '\n') {
                    holder.registerProblem(
                        file.firstChild,
                        EMPTY_LINE_ON_TOP_OF_FILE,
                        ProblemHighlightType.WEAK_WARNING
                    )
                }
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
                    if (decl.nextSibling is PsiWhiteSpace) {
                        var newLineEncountered = 0
                        decl.nextSibling.text.forEach { chr ->
                            if (chr == '\n') {
                                newLineEncountered += 1
                            }
                        }
                        if (newLineEncountered < 1) {
                            holder.registerProblem(decl, NEWLINE_AFTER_BRACE, ProblemHighlightType.WEAK_WARNING)
                        }
                    } else {
                        holder.registerProblem(decl, NEWLINE_AFTER_BRACE, ProblemHighlightType.WEAK_WARNING)
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
                            WS_BEFORE_DECL,
                            ProblemHighlightType.WEAK_WARNING
                        )
                    }
                }
                val whiteSpaceBetweenTypeAndName =
                    stmt?.children?.filterIsInstance<OCTypeElement>()?.first()?.nextSibling
                if (whiteSpaceBetweenTypeAndName is PsiWhiteSpace) {
                    whiteSpaceBetweenTypeAndName.text?.forEach {
                        if (it != '\t') {
                            holder.registerProblem(
                                stmt,
                                SPACE_SHOULD_BE_TAB,
                                ProblemHighlightType.WEAK_WARNING
                            )
                        }
                    }
                }
            }

            override fun visitDeclaration(stmt: OCDeclaration?) {
                if (stmt?.parent is OCParameterList) return
                val whiteSpaceBetweenTypeAndName =
                    stmt?.children?.filterIsInstance<OCTypeElement>()?.first()?.nextSibling
                if (whiteSpaceBetweenTypeAndName is PsiWhiteSpace) {
                    whiteSpaceBetweenTypeAndName.text?.forEach {
                        if (it != '\t') {
                            holder.registerProblem(
                                stmt,
                                SPACE_SHOULD_BE_TAB,
                                ProblemHighlightType.WEAK_WARNING
                            )
                        }
                    }
                }
            }
        }
    }
}