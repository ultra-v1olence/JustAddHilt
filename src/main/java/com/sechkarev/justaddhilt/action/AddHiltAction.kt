package com.sechkarev.justaddhilt.action

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.sechkarev.justaddhilt.notification.ShowBalloonNotification
import com.sechkarev.justaddhilt.usecase.hilt.annotation.AddHiltAnnotationToApplicationClasses
import com.sechkarev.justaddhilt.usecase.hilt.dependency.AddHiltDependenciesToAndroidModules
import com.sechkarev.justaddhilt.usecase.project.build.AreAndroidModulesPresentInProject
import com.sechkarev.justaddhilt.usecase.project.build.SyncProjectWithGradleFiles
import org.jetbrains.kotlin.idea.util.application.runWriteAction

class AddHiltAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        if (!project.service<AreAndroidModulesPresentInProject>()()) {
            showNoAndroidModulesMessage(project)
            return
        }

        var dependenciesWereAdded = false
        executeCommand {
            runWriteAction {
                dependenciesWereAdded = project.service<AddHiltDependenciesToAndroidModules>()()
            }
        }
        if (dependenciesWereAdded) {
            project.service<SyncProjectWithGradleFiles>()(
                onSyncFinished = {
                    addHiltAnnotationToApplicationClasses(project)
                }
            )
        } else {
            addHiltAnnotationToApplicationClasses(project)
        }
    }

    private fun addHiltAnnotationToApplicationClasses(project: Project) {
        var codeWasAdded = false
        executeCommand {
            runWriteAction {
                codeWasAdded = project.service<AddHiltAnnotationToApplicationClasses>()()
            }
        }
        if (codeWasAdded) {
            showHiltWasAddedMessage(project)
        } else {
            showHiltAlreadyPresentMessage(project)
        }
    }

    private fun showNoAndroidModulesMessage(project: Project) {
        project.service<ShowBalloonNotification>()(
            "Looks like this is not an Android project. Hilt can't be added to it.",
            NotificationType.WARNING,
        ) // todo: figure out how localization works here
    }

    private fun showHiltWasAddedMessage(project: Project) {
        project.service<ShowBalloonNotification>()(
            "Hilt was successfully added to the project.",
            NotificationType.INFORMATION,
        )
    }

    private fun showHiltAlreadyPresentMessage(project: Project) {
        project.service<ShowBalloonNotification>()(
            "No code was added: Hilt is already added to the project.",
            NotificationType.WARNING,
        )
    }
}
