package norminette

import com.intellij.util.NotNullProducer
import com.jetbrains.cidr.lang.inspections.OCInspection

class NorminetteInspectionProvider : NotNullProducer<List<Class<out Any>>> {
    override fun produce(): List<Class<out OCInspection>> = listOf(
        MultipleDeclarationsInspection()::class.java
    )
}

