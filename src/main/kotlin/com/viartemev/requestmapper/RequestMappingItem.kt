package com.viartemev.requestmapper

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod

class RequestMappingItem(private val psiElement: PsiElement, private val urlPath: String, private val requestMethod: String) : PsiElementNavigationItem {

    private val navigationElement = psiElement.navigationElement as? Navigatable

    override fun getName(): String = this.requestMethod + " " + this.urlPath

    override fun getPresentation(): ItemPresentation = RequestMappingItemPresentation(this)

    override fun getTargetElement(): PsiElement = psiElement

    override fun navigate(requestFocus: Boolean) = navigationElement?.navigate(requestFocus) ?: Unit

    override fun canNavigate(): Boolean = navigationElement?.canNavigate() ?: false

    override fun canNavigateToSource(): Boolean = true

    override fun toString(): String {
        return "RequestMappingItem(psiElement=$psiElement, urlPath='$urlPath', requestMethod='$requestMethod', navigationElement=$navigationElement)"
    }

    internal class RequestMappingItemPresentation(private val item: RequestMappingItem) : ItemPresentation {
        fun getRequestMethod() = item.requestMethod

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
