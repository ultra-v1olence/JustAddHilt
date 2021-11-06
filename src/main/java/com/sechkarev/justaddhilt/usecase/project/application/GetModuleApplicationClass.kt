package com.sechkarev.justaddhilt.usecase.project.application

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.components.Service
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.util.ClassUtil
import com.sechkarev.justaddhilt.usecase.project.manifest.GetApplicationNameFromManifest
import org.jetbrains.android.dom.manifest.getPrimaryManifestXml

@Service
class GetModuleApplicationClass(private val module: Module) {

    private val getApplicationNameFromManifest = module.getService(GetApplicationNameFromManifest::class.java)

    operator fun invoke(): PsiClass? {
        val primaryManifestXml = module.androidFacet?.getPrimaryManifestXml()
        val packageName = primaryManifestXml?.packageName
        val applicationName = getApplicationNameFromManifest()
        val fullClassName = packageName + applicationName
        return ClassUtil.findPsiClass(PsiManager.getInstance(module.project), fullClassName)
    }
}