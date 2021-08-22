package com.samkortekaas.norminette.fixes

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class TestFix(private val element: PsiElement) : BaseIntentionAction() {

    override fun getText(): String {
        return "Remove spaces"
    }

    override fun getFamilyName(): String {
        return "Test fix add bla"
    }

    override fun isAvailable(p0: Project, p1: Editor?, p2: PsiFile?): Boolean {
        return true
    }

    override fun invoke(p0: Project, p1: Editor?, p2: PsiFile?) {
        print("Invoded intention on: ${element.text}")
    }


}