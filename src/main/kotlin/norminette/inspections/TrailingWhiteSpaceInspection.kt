package norminette.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.descendantsOfType
import com.jetbrains.cidr.lang.psi.*
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor
import org.intellij.markdown.flavours.gfm.table.GitHubTableMarkerProvider.Companion.contains

class TrailingWhiteSpaceInspection : Norminette() {
    override fun runForWholeFile(): Boolean {
        return true
    }

    override fun worksWithClangd(): Boolean {
        return true
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            private val TR_WS = "Trailing white space is not allowed"
            private val EMPTY_LINE = "Empty lines cannot contains characters"

            override fun visitWhiteSpace(space: PsiWhiteSpace) {
                var numOfNLS = 0
                space.text.forEach { if (it == '\n') numOfNLS += 1 }
                if (numOfNLS == 1 && space.text.first() != '\n') {
                    holder.registerProblem(space, TR_WS, ProblemHighlightType.WEAK_WARNING)
                }
                else if (numOfNLS > 1) {
                    space.text.forEachIndexed { index, c ->
                        if (index < numOfNLS && c != '\n') {
                            holder.registerProblem(space, EMPTY_LINE, ProblemHighlightType.WEAK_WARNING)
                        }
                    }
                }
            }
        }
    }
}