package com.samkortekaas.norminette.services

import com.samkortekaas.norminette.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {
    init {
        println(MyBundle.message("projectService", project.name))
    }
}
