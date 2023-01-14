package com.viartemev.requestmapper.contributor

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.asJava.toLightAnnotation
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex

class KotlinRequestMappingContributor : RequestMappingByNameContributor() {
    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> {
        val project = scope.project ?: return emptySequence()
        return KotlinAnnotationsIndex
            .getInstance()
            .get(annotationName, project, scope)
            .asSequence()
            .mapNotNull { it.toLightAnnotation() }
    }
}
