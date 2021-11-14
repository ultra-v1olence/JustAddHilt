package com.sechkarev.justaddhilt.notifications

import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service
class ShowNoAndroidModulesNotification(project: Project) {

    private val showBalloonNotification = project.service<ShowBalloonNotification>()

    operator fun invoke() {
        showBalloonNotification(
            "Looks like this is not an Android project. Hilt can't be added to it.",
            NotificationType.WARNING,
        )
    }
}