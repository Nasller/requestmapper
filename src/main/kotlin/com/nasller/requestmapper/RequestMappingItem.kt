package com.nasller.requestmapper

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.openapi.util.Iconable
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import javax.swing.Icon

class RequestMappingItem(
    private val psiElement: PsiElement,
    private val url: String,
    private val urlPath: String,
    private val requestMethod: String,
    private val params: String = "",
) : PsiElementNavigationItem {
    private val navigationElement = psiElement.navigationElement as? Navigatable

    override fun getName() = urlPath

    override fun getPresentation(): ItemPresentation = RequestMappingItemPresentation(this)

    override fun getTargetElement() = psiElement

    override fun navigate(requestFocus: Boolean) = navigationElement?.navigate(requestFocus) ?: Unit

    override fun canNavigate() = navigationElement?.canNavigate() ?: false

    override fun canNavigateToSource() = true

    override fun toString() =
        "RequestMappingItem(psiElement=$psiElement, requestMethod='$requestMethod', url='$url', urlPath='$urlPath', params='$params', navigationElement=$navigationElement)"

    internal class RequestMappingItemPresentation(private val item: RequestMappingItem) : ItemPresentation {
        val containingFile: PsiFile? = item.targetElement.containingFile

        fun getRequestMethod() = item.requestMethod

        fun getUrl() = item.url

        fun getParams() = item.params

        override fun getPresentableText() = item.urlPath

        override fun getLocationString(): String {
            return when (val psiElement = item.targetElement) {
                is PsiMethod -> (psiElement.containingClass?.name ?: containingFile?.name ?: "unknownFile") + "." + psiElement.name
                is PsiClass -> psiElement.name ?: containingFile?.name ?: "unknownFile"
                else -> "unknownLocation"
            }
        }

        override fun getIcon(b: Boolean): Icon? = containingFile?.getIcon(Iconable.ICON_FLAG_READ_STATUS)
    }
}