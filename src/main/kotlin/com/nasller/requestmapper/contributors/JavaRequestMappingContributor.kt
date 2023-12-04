package com.nasller.requestmapper.contributors

import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex
import com.intellij.psi.search.GlobalSearchScope

class JavaRequestMappingContributor : RequestMappingByNameContributor() {
    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> {
        val project = scope.project ?: return emptySequence()
        return JavaAnnotationIndex
            .getInstance()
            .get(annotationName, project, scope)
            .asSequence()
    }

    override fun getLanguageRef(): LanguageRef = LanguageRef.forLanguage(JavaLanguage.INSTANCE)
}