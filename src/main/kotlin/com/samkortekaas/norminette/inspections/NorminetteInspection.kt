package com.samkortekaas.norminette.inspections

import com.intellij.codeInspection.ExternalAnnotatorInspectionVisitor
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.jetbrains.cidr.lang.psi.visitors.OCVisitor
import com.samkortekaas.norminette.annotator.NorminetteAnnotator


class NorminetteInspection : LocalInspectionTool() {

    val annotator = NorminetteAnnotator()

    override fun getGroupDisplayName(): String = "C/C++"

    override fun getDisplayName(): String = "Norminette"

    override fun isEnabledByDefault(): Boolean = true

    override fun runForWholeFile(): Boolean = true

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : OCVisitor() {
            override fun visitFile(file: PsiFile) {
                val problemDescriptors = ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(
                    file,
                    holder.manager,
                    false,
                    annotator
                )

                for (descriptor in problemDescriptors) {
                    holder.registerProblem(
                        descriptor.psiElement,
                        descriptor.descriptionTemplate
                    )
                }
            }
        }
    }
}