package com.viartemev.requestmapper.contributors

import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.search.GlobalSearchScope

interface RequestMappingContributor : ChooseByNameContributorEx {

    fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation>

    fun getLanguageRef(): LanguageRef

    companion object{
        private val extensionPoints = ExtensionPointName.create<RequestMappingContributor>("com.viartemev.requestmapper.requestMappingContributor")

        fun getExtensions(): List<RequestMappingContributor> {
            return extensionPoints.extensionList
        }
    }
}