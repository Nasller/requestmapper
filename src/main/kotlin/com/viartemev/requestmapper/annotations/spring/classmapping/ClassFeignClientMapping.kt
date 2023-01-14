package com.viartemev.requestmapper.annotations.spring.classmapping

import com.intellij.psi.PsiAnnotation
import com.viartemev.requestmapper.annotations.PathAnnotation
import com.viartemev.requestmapper.annotations.spring.SpringMappingAnnotation

class ClassFeignClientMapping(val annotation: PsiAnnotation) : SpringClassMappingAnnotation {
    override fun fetchClassMapping(): List<ClassMappingData> {
        val pathAnnotation = PathAnnotation(annotation)
        val name = pathAnnotation.fetchMappings(SpringMappingAnnotation.VALUE).firstOrNull() ?:
        pathAnnotation.fetchMappings(SpringMappingAnnotation.NAME).firstOrNull() ?: ""
        var url = pathAnnotation.fetchMappings(SpringMappingAnnotation.URL).firstOrNull() ?: ""
        val path = pathAnnotation.fetchMappings(SpringMappingAnnotation.PATH).firstOrNull() ?: ""

        // analogue from org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget
        if (url.isBlank()) {
            url = name
        }
        return listOf(ClassMappingData(url,getCleanPath(path)))
    }

    // analogue from org.springframework.cloud.openfeign.FeignClientFactoryBean#cleanPath
    private fun getCleanPath(path: String): String {
        var normalizedPath = if (!path.startsWith("/")) "/$path" else path
        normalizedPath = if (normalizedPath.endsWith("/")) normalizedPath.substring(0, normalizedPath.length - 1) else normalizedPath
        return normalizedPath
    }
}