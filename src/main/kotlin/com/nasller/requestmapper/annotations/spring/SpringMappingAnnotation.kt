package com.nasller.requestmapper.annotations.spring

import com.intellij.psi.*
import com.nasller.requestmapper.RequestMappingItem
import com.nasller.requestmapper.annotations.MappingAnnotation
import com.nasller.requestmapper.annotations.PathAnnotation
import com.nasller.requestmapper.annotations.UrlFormatter
import com.nasller.requestmapper.annotations.extraction.PsiExpressionExtractor
import com.nasller.requestmapper.annotations.spring.classmapping.SpringClassMappingAnnotation
import com.nasller.requestmapper.model.Path
import com.nasller.requestmapper.model.PathParameter
import com.nasller.requestmapper.utils.fetchAnnotatedMethod

abstract class SpringMappingAnnotation(
    val psiAnnotation: PsiAnnotation,
    private val urlFormatter: UrlFormatter = SpringUrlFormatter
) : MappingAnnotation {
    abstract fun extractMethod(): String

    override fun values(): List<RequestMappingItem> = fetchRequestMappingItem(psiAnnotation, psiAnnotation.fetchAnnotatedMethod(), extractMethod())

    private fun fetchRequestMappingItem(annotation: PsiAnnotation, psiMethod: PsiMethod, methodName: String): List<RequestMappingItem> {
        val classMappings = SpringClassMappingAnnotation.fetchMappingsFromClass(psiMethod)
        val methodMappings = fetchMappingsFromMethod(annotation, psiMethod)
        val paramsMappings = PathAnnotation(annotation).fetchMappings(PARAMS).ifEmpty { listOf("") }
        return classMappings.flatMap { clazz ->
            methodMappings.flatMap { method ->
                paramsMappings.map { param ->
                    RequestMappingItem(psiMethod, clazz.url, urlFormatter.format(clazz.path, method), methodName, param)
                }
            }
        }
    }

    private fun fetchMappingsFromMethod(annotation: PsiAnnotation, method: PsiMethod): List<String> {
        val parametersNameWithType = method.parameterList.parameters
            .mapNotNull { PathParameter(it).extractParameterNameWithType(SPRING_PATH_VARIABLE_CLASS, ::extractParameterNameFromAnnotation) }
            .toMap()
        return SpringClassMappingAnnotation.fetchPathValueMapping(annotation).map { Path(it).addPathVariablesTypes(parametersNameWithType).toFullPath() }
    }

    private fun extractParameterNameFromAnnotation(annotation: PsiAnnotation, defaultValue: String): String {
        val pathVariableValue = annotation.findAttributeValue(VALUE)
        val pathVariableName = annotation.findAttributeValue(NAME)
        val valueAttribute = extractPsiAnnotation(pathVariableValue, defaultValue)
        return if (valueAttribute != defaultValue) valueAttribute else extractPsiAnnotation(pathVariableName, defaultValue)
    }

    private fun extractPsiAnnotation(psiAnnotationMemberValue: PsiAnnotationMemberValue?, defaultValue: String): String {
        return when (psiAnnotationMemberValue) {
            is PsiLiteralExpression -> {
                PsiExpressionExtractor.extractExpression(psiAnnotationMemberValue).ifBlank { defaultValue }
            }
            is PsiReferenceExpression -> {
                PsiExpressionExtractor.extractExpression(psiAnnotationMemberValue).ifBlank { defaultValue }
            }
            else -> defaultValue
        }
    }

    companion object {
        const val URL = "url"
        const val PATH = "path"
        const val NAME = "name"
        const val VALUE = "value"
        private const val PARAMS = "params"
        private const val SPRING_PATH_VARIABLE_CLASS = "org.springframework.web.bind.annotation.PathVariable"
    }
}