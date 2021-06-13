package com.github.skrtks.norminette.linter

import com.github.skrtks.norminette.inspections.Norminette
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.jetbrains.cidr.lang.psi.OCFile
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor
import settings.Option
import settings.Settings
import java.io.File

class NorminetteLinterInspection : Norminette() {
    override fun runForWholeFile(): Boolean {
        return true
    }

    override fun worksWithClangd(): Boolean {
        return true
    }
//    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
//        val document = FileDocumentManager.getInstance().getDocument(file.virtualFile)
//        return lint(file, manager, document!!)
//    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitOCFile(file: OCFile?) {
//                val document = PsiDocumentManager.getInstance(file?.project!!).getDocument(file!!)
                FileDocumentManager.getInstance().saveAllDocuments()
                val norminettePath = Settings.get(Option.OPTION_KEY_CPPLINT)
                var pythonPath = Settings.get(Option.OPTION_KEY_PYTHON)
                var norminetteOptions = Settings.get(Option.OPTION_KEY_CPPLINT_OPTIONS)


                // setup process
                // run norm on file
                // parse output line by line
                // create problem descriptor
                // return descr
                // repeat until last line

                val localFile = file?.virtualFile?.path

                val res = "norminette $localFile".runCommand(File(norminettePath))

                parseResult(res, file!!, holder)
            }
        }
    }

    fun parseResult(res: String?, file: PsiFile, holder: ProblemsHolder) {
        val errors = res?.split("\n")
        errors?.mapNotNull { if (it.startsWith("Error: ")) parseError(it, file, holder) else null }
            ?.toTypedArray()
            ?: emptyArray()
    }

    fun parseError(error: String, file: PsiFile, holder: ProblemsHolder) {
        val blocks = error.split("\\s".toRegex()).filter { it != "" }
        val shortCode = blocks[1]
        val line = blocks[blocks.indexOf("(line:") + 1].filter { it.isDigit() }.toInt()
        val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
        val lineStartOffset = document?.getLineStartOffset(line)
        val lineEndOffset = document?.getLineEndOffset(line)

        holder.registerProblem(
            file,
            shortCode,
            ProblemHighlightType.WEAK_WARNING
        )
    }

}