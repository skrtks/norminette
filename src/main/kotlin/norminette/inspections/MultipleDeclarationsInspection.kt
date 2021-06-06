package norminette.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.cidr.lang.psi.OCDeclarationStatement
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor

class MultipleDeclarationsInspection : Norminette() {
    override fun runForWholeFile(): Boolean {
        return true
    }

    override fun worksWithClangd(): Boolean {
        return true
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            private val DESCRIPTION_TEMPLATE = "Multiple declarations on a single line"
            override fun visitDeclarationStatement(stmt: OCDeclarationStatement?) {
                // Identified an expression with potential problems, add to list with fix object.
                val size = stmt?.declaration?.declarators?.size
                if (size != null && size > 1) {
                    holder.registerProblem(stmt, DESCRIPTION_TEMPLATE, ProblemHighlightType.WEAK_WARNING)
                }
            }
        }
    }

//    /**
//     * This class provides a solution to inspection problem expressions by manipulating the PSI tree to use 'a.equals(b)'
//     * instead of '==' or '!='.
//     */
//    private class CriQuickFix : LocalQuickFix {
//        /**
//         * Returns a partially localized string for the quick fix intention.
//         * Used by the test code for this plugin.
//         *
//         * @return Quick fix short name.
//         */
//        override fun getName(): String {
//            return "Split over multiple lines"
//        }
//
//        /**
//         * This method manipulates the PSI tree to replace 'a==b' with 'a.equals(b)' or 'a!=b' with '!a.equals(b)'.
//         *
//         * @param project    The project that contains the file being edited.
//         * @param descriptor A problem found by this inspection.
//         */
//        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
//            println("Applying fix!")
//        }
//
//        override fun getFamilyName(): String {
//            return name
//        }
//    }
}