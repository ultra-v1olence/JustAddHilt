package com.sechkarev.justaddhilt.usecases.project.application

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.module.Module
import com.sechkarev.justaddhilt.usecases.project.manifest.GetApplicationNameFromManifest
import org.jetbrains.android.dom.manifest.getPrimaryManifestXml

class IsApplicationClassGenerationRequiredForModule(private val module: Module) {

    private val getCurrentApplicationName = GetApplicationNameFromManifest(module)

    operator fun invoke(): Boolean {
        val androidFacet = module.androidFacet ?: return false
        val packageName = androidFacet.getPrimaryManifestXml()?.packageName
        val currentApplicationName = getCurrentApplicationName()
        return currentApplicationName == null && packageName != null
    }
}