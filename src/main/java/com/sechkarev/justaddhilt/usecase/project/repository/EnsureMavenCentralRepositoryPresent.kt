package com.sechkarev.justaddhilt.usecase.project.repository

import com.android.tools.idea.gradle.dsl.api.ProjectBuildModel
import com.android.tools.idea.gradle.dsl.api.repositories.RepositoriesModel
import com.android.tools.idea.gradle.dsl.model.repositories.MavenCentralRepositoryModel
import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.util.application.runWriteAction

@Service
class EnsureMavenCentralRepositoryPresent(private val project: Project) {

    operator fun invoke(): Boolean {
        ProjectBuildModel
            .get(project)
            .projectBuildModel
            ?.apply {
                if (repositories().mavenCentralPresent()) return false
                repositories().addRepositoryByMethodName(MavenCentralRepositoryModel.MAVEN_CENTRAL_METHOD_NAME)
                applyChanges()
                psiElement?.let { CodeStyleManager.getInstance(project).reformat(it) }
            }
        return true
        // fixme: when there is no allprojects { ... } block, it is not created!
        // todo: also, the settings file isn't taken into account. if this is present, addition fails (
        //  dependencyResolutionManagement {
        //    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        //    ...
        //    }
        // )
    }

    private fun RepositoriesModel.mavenCentralPresent() =
        containsMethodCall(MavenCentralRepositoryModel.MAVEN_CENTRAL_METHOD_NAME)

}