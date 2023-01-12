package com.viartemev.requestmapper.contributor

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnnotation
import com.intellij.util.indexing.FindSymbolParameters
import org.jetbrains.kotlin.asJava.toLightAnnotation
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex

class KotlinRequestMappingContributor : RequestMappingByNameContributor() {

    override fun getAnnotationSearchers(annotationName: String, project: Project, includeNonProjectItems: Boolean): Sequence<PsiAnnotation> {
        return KotlinAnnotationsIndex
            .getInstance()
            .get(annotationName, project, FindSymbolParameters.searchScopeFor(project,includeNonProjectItems))
            .asSequence()
            .mapNotNull { it.toLightAnnotation() }
    }
}
