package com.github.skrtks.norminette.inspections

import com.intellij.util.NotNullProducer
import com.jetbrains.cidr.lang.inspections.OCInspection

class NorminetteInspectionProvider : NotNullProducer<List<Class<out Any>>> {
    override fun produce(): List<Class<out OCInspection>> = listOf(
//        MultipleDeclarationsInspection()::class.java,
//        DeclarationAssignmentSingleLineInspection()::class.java,
//        DeclarationWhiteSpaceInspection()::class.java,
//        TrailingWhiteSpaceInspection()::class.java,
//        FunctionLineLimit()::class.java,
//        NorminetteLinterInspection()::class.java
    )
}

