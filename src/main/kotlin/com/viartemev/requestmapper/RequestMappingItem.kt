package com.viartemev.requestmapper

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod

class RequestMappingItem(
    private val psiElement: PsiElement,
    private val url: String,
    private val urlPath: String,
    private val requestMethod: String,
    private val params: String = "",
) : PsiElementNavigationItem {
    private val navigationElement = psiElement.navigationElement as? Navigatable

    override fun getName(): String = urlPath

    override fun getPresentation(): ItemPresentation = RequestMappingItemPresentation(this)

    override fun getTargetElement(): PsiElement = psiElement

    override fun navigate(requestFocus: Boolean) = navigationElement?.navigate(requestFocus) ?: Unit

    override fun canNavigate(): Boolean = navigationElement?.canNavigate() ?: false

    override fun canNavigateToSource(): Boolean = true

    override fun toString(): String {
        return "RequestMappingItem(psiElement=$psiElement, requestMethod='$requestMethod', url='$url', urlPath='$urlPath', params='$params', navigationElement=$navigationElement)"
    }

    internal class RequestMappingItemPresentation(private val item: RequestMappingItem) : ItemPresentation {
        fun getRequestMethod() = item.requestMethod

        fun getUrl() = item.url

        fun getParams() = item.params

        override fun getPresentableText() = item.urlPath

        override fun getLocationString(): String {
            return when (val psiElement = item.psiElement) {
                is PsiMethod -> (psiElement.containingClass?.name ?: psiElement.containingFile?.name ?: "unknownFile") + "." + psiElement.name
                is PsiClass -> psiElement.name ?: psiElement.containingFile?.name ?: "unknownFile"
                else -> "unknownLocation"
            }
        }

        override fun getIcon(b: Boolean) = RequestMapperIcons.SEARCH
    }
}