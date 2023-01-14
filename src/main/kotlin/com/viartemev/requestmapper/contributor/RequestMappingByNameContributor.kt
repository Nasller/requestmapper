package com.viartemev.requestmapper.contributor

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import com.viartemev.requestmapper.RequestMappingItem
import com.viartemev.requestmapper.annotations.MappingAnnotation.Companion.mappingAnnotation
import com.viartemev.requestmapper.annotations.MappingAnnotation.Companion.supportedAnnotations
import com.viartemev.requestmapper.utils.isMethodAnnotation

abstract class RequestMappingByNameContributor(
    private var navigationItems: List<RequestMappingItem> = emptyList()
) : ChooseByNameContributorEx {

    abstract fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation>

    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        navigationItems = supportedAnnotations
            .flatMap { annotation -> findRequestMappingItems(annotation, scope) }
        ContainerUtil.process(navigationItems.map { it.name }.distinct().toTypedArray(), processor)
    }

    override fun processElementsWithName(name: String, processor: Processor<in NavigationItem>, parameters: FindSymbolParameters) {
        ContainerUtil.process(navigationItems.filter { it.name == name }.toTypedArray(), processor)
    }

    private fun findRequestMappingItems(annotationName: String, scope: GlobalSearchScope): List<RequestMappingItem> {
        return getAnnotationSearchers(annotationName, scope)
            .filter { it.isMethodAnnotation() }
            .map { annotation -> mappingAnnotation(annotationName, annotation) }
            .flatMap { mappingAnnotation -> mappingAnnotation.values().asSequence() }
            .toList()
    }
}
