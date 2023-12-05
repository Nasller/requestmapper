package com.nasller.requestmapper.annotations.spring.classmapping

import com.intellij.psi.PsiAnnotation
import com.nasller.requestmapper.annotations.PathAnnotation
import com.nasller.requestmapper.annotations.spring.SpringMappingAnnotation

class ClassFeignClientMapping(private val annotation: PsiAnnotation) : SpringClassMappingAnnotation {
    override fun fetchClassMapping(): List<ClassMappingData> {
        val pathAnnotation = PathAnnotation(annotation)
        var url = pathAnnotation.fetchMappings(SpringMappingAnnotation.URL).firstOrNull() ?: ""
        val path = pathAnnotation.fetchMappings(SpringMappingAnnotation.PATH).firstOrNull() ?: ""
        // analogue from org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget
        if (url.isBlank()) {
            val value = pathAnnotation.fetchMappings(SpringMappingAnnotation.VALUE).firstOrNull()
            url = if(value.isNullOrBlank()) {
                pathAnnotation.fetchMappings(SpringMappingAnnotation.NAME).firstOrNull() ?: ""
            } else value
        }
        return listOf(ClassMappingData(url, getCleanPath(path)))
    }

    // analogue from org.springframework.cloud.openfeign.FeignClientFactoryBean#cleanPath
    private fun getCleanPath(path: String): String {
        var normalizedPath = if (!path.startsWith("/")) "/$path" else path
        normalizedPath = if (normalizedPath.endsWith("/")) normalizedPath.substring(0, normalizedPath.length - 1) else normalizedPath
        return normalizedPath
    }
}