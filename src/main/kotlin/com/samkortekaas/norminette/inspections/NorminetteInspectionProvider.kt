package com.samkortekaas.norminette.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.util.NotNullProducer
import com.jetbrains.cidr.lang.inspections.OCInspection

class NorminetteInspectionProvider : NotNullProducer<List<Class<out Any>>> {
    override fun produce(): List<Class<out LocalInspectionTool>> = listOf(
        NorminetteInspection()::class.java,
    )
}

