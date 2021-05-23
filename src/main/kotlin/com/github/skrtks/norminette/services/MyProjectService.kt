package com.github.skrtks.norminette.services

import com.github.skrtks.norminette.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
