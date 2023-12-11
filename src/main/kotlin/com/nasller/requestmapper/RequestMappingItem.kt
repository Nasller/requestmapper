package com.nasller.requestmapper

import com.intellij.ide.util.ModuleRendererFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItemFileStatus
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.util.Iconable
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.VfsPresentationUtil
import com.intellij.pom.Navigatable
import com.intellij.problems.WolfTheProblemSolver
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import java.awt.Color

class RequestMappingItem(
    private val psiElement: PsiElement,
    private val url: String,
    private val urlPath: String,
    private val requestMethod: String,
    private val params: String = "",
) : DataProvider, PsiElementNavigationItem {
    val textIcon = ModuleRendererFactory.findInstance(this).getModuleTextWithIcon(targetElement)

    private var bgColor: Color? = null
    private var errorEffectType: EffectType? = null
    private var foregroundColor: Color? = null
    private var errorEffectColor: Color? = null
    private val containingFile: PsiFile? = targetElement.containingFile
    private val locationString = when (val psiElement = targetElement) {
        is PsiMethod -> (psiElement.containingClass?.name ?: containingFile?.name ?: "unknownFile") + "." + psiElement.name
        is PsiClass -> psiElement.name ?: containingFile?.name ?: "unknownFile"
        else -> "unknownLocation"
    }
    private val icon = containingFile?.getIcon(Iconable.ICON_FLAG_READ_STATUS)
    private val navigationElement = psiElement.navigationElement as? Navigatable
    init {
        val status = NavigationItemFileStatus.get(this)
        if(status !== FileStatus.NOT_CHANGED) {
            foregroundColor = status.color
        }
        if (containingFile?.isValid == true) {
            getVirtualFile(containingFile)?.let {
                val project = containingFile.project
                VfsPresentationUtil.getFileBackgroundColor(project, it)?.apply { bgColor = this }
                if (WolfTheProblemSolver.getInstance(project).isProblemFile(it)) {
                    EditorColorsManager.getInstance().schemeForCurrentUITheme.getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES).let {text ->
                        errorEffectType = text.effectType ?: EffectType.WAVE_UNDERSCORE
                        errorEffectColor = text.effectColor
                    }
                }
            }
        }
    }

    override fun getName() = urlPath

    override fun getPresentation(): ItemPresentation = RequestMappingItemPresentation(this)

    override fun getTargetElement() = psiElement

    override fun navigate(requestFocus: Boolean) = navigationElement?.navigate(requestFocus) ?: Unit

    override fun canNavigate() = navigationElement?.canNavigate() ?: false

    override fun canNavigateToSource() = true

    override fun toString() =
        "RequestMappingItem(psiElement=$psiElement, requestMethod='$requestMethod', url='$url', urlPath='$urlPath', params='$params', navigationElement=$navigationElement)"

    override fun getData(dataId: String): Any? {
        return when (dataId) {
            CommonDataKeys.PSI_ELEMENT.name -> psiElement
            else -> null
        }
    }

    private companion object{
        private fun getVirtualFile(containingFile: PsiFile): VirtualFile? {
            var virtualFile = containingFile.virtualFile
            if (virtualFile == null) {
                val originalFile = containingFile.originalFile
                if (originalFile !== containingFile && originalFile.isValid) {
                    virtualFile = originalFile.virtualFile
                }
            }
            return virtualFile
        }
    }

    internal class RequestMappingItemPresentation(private val item: RequestMappingItem) : ItemPresentation {
        fun getRequestMethod() = item.requestMethod

        fun getUrl() = item.url

        fun getParams() = item.params

        fun getForegroundColor() = item.foregroundColor

        fun getBgColor() = item.bgColor

        fun getErrorEffectType() = item.errorEffectType

        fun getErrorEffectColor() = item.errorEffectColor

        override fun getPresentableText() = item.urlPath

        override fun getLocationString() = item.locationString

        override fun getIcon(b: Boolean) = item.icon
    }
}