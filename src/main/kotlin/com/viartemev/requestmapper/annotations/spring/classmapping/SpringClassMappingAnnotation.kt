package com.viartemev.requestmapper.annotations.spring.classmapping

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod
import com.viartemev.requestmapper.annotations.PathAnnotation
import com.viartemev.requestmapper.annotations.spring.SpringMappingAnnotation

interface SpringClassMappingAnnotation {
    fun fetchClassMapping(): List<ClassMappingData>

    companion object {
        private const val SPRING_REQUEST_MAPPING_CLASS = "org.springframework.web.bind.annotation.RequestMapping"
        private const val SPRING_OPEN_FEIGN_CLASS = "org.springframework.cloud.openfeign.FeignClient"
        private const val SPRING_NETFLIX_FEIGN_CLASS = "org.springframework.cloud.netflix.feign.FeignClient"

        fun fetchMappingsFromClass(psiMethod: PsiMethod): List<ClassMappingData> {
            val classMapping = psiMethod.containingClass?.modifierList?.annotations?.filterNotNull()
                ?.flatMap { mappingAnnotation(it).fetchClassMapping() } ?: emptyList()
            return classMapping.ifEmpty { listOf(ClassMappingData("","")) }
        }

        fun fetchPathValueMapping(annotation: PsiAnnotation): List<String> {
            val pathAnnotation = PathAnnotation(annotation)
            return pathAnnotation.fetchMappings(SpringMappingAnnotation.PATH).ifEmpty {
                pathAnnotation.fetchMappings(SpringMappingAnnotation.VALUE).ifEmpty { listOf("") }
            }
        }

        private fun mappingAnnotation(psiAnnotation: PsiAnnotation): SpringClassMappingAnnotation {
            return when (psiAnnotation.qualifiedName) {
                SPRING_REQUEST_MAPPING_CLASS -> ClassRequestMapping(psiAnnotation)
                SPRING_OPEN_FEIGN_CLASS -> ClassFeignClientMapping(psiAnnotation)
                SPRING_NETFLIX_FEIGN_CLASS -> ClassFeignClientMapping(psiAnnotation)
                else -> ClassUnknownAnnotation()
            }
        }
    }
}

data class ClassMappingData(val url: String,val path: String)