package com.viartemev.requestmapper.contributor

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex
import com.intellij.util.indexing.FindSymbolParameters

class JavaRequestMappingContributor : RequestMappingByNameContributor() {

    override fun getAnnotationSearchers(annotationName: String, project: Project, includeNonProjectItems: Boolean): Sequence<PsiAnnotation> {
        return JavaAnnotationIndex
            .getInstance()
            .get(annotationName, project, FindSymbolParameters.searchScopeFor(project,includeNonProjectItems))
            .asSequence()
    }
}
