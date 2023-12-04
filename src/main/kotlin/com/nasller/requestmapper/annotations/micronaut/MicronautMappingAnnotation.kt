package com.nasller.requestmapper.annotations.micronaut

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiReferenceExpression
import com.nasller.requestmapper.RequestMappingItem
import com.nasller.requestmapper.annotations.MappingAnnotation
import com.nasller.requestmapper.annotations.PathAnnotation
import com.nasller.requestmapper.annotations.UrlFormatter
import com.nasller.requestmapper.annotations.extraction.PsiExpressionExtractor.extractExpression
import com.nasller.requestmapper.model.Path
import com.nasller.requestmapper.model.PathParameter
import com.nasller.requestmapper.utils.fetchAnnotatedMethod
import com.nasller.requestmapper.utils.unquote

abstract class MicronautMappingAnnotation(
    val psiAnnotation: PsiAnnotation,
    private val urlFormatter: UrlFormatter = MicronautUrlFormatter
) : MappingAnnotation {

    override fun values(): List<RequestMappingItem> {
        return fetchRequestMappingItem(psiAnnotation, psiAnnotation.fetchAnnotatedMethod(), extractMethod())
    }

    abstract fun extractMethod(): String

    private fun fetchRequestMappingItem(annotation: PsiAnnotation, psiMethod: PsiMethod, method: String): List<RequestMappingItem> {
        val classMapping = fetchMappingFromClass(psiMethod)
        val methodMapping = fetchMappingFromMethod(annotation, psiMethod)
        return listOf(RequestMappingItem(psiMethod, "", urlFormatter.format(classMapping, methodMapping), method))
    }

    private fun fetchMappingFromClass(psiMethod: PsiMethod): String {
        return psiMethod
            .containingClass
            ?.modifierList
            ?.annotations
            ?.flatMap { extractPathFromMicronautPsiAnnotations(it) }
            ?.firstOrNull() ?: ""
    }

    private fun extractPathFromMicronautPsiAnnotations(psiAnnotation: PsiAnnotation) = when (psiAnnotation.qualifiedName) {
        CONTROLLER_ANNOTATION -> PathAnnotation(psiAnnotation).fetchMappings(ATTRIBUTE_NAME)
        else -> emptyList()
    }

    private fun fetchMappingFromMethod(annotation: PsiAnnotation, method: PsiMethod): String {
        val parametersNameWithType = method
            .parameterList
            .parameters
            .mapNotNull {
                PathParameter(it).extractParameterNameWithType(PATH_VARIABLE_ANNOTATION, ::extractParameterNameFromAnnotation)
                    ?: Pair(it.name, it.type.presentableText.unquote())
            }
            .toMap()

        return PathAnnotation(annotation).fetchMappings(ATTRIBUTE_NAME)
            .map { Path(it).addPathVariablesTypes(parametersNameWithType).toFullPath() }
            .firstOrNull() ?: ""
    }

    private fun extractParameterNameFromAnnotation(annotation: PsiAnnotation, defaultValue: String): String {
        return when (val pathVariableValue = annotation.findAttributeValue(ATTRIBUTE_NAME)) {
            is PsiLiteralExpression -> {
                val expression = extractExpression(pathVariableValue)
                if (expression.isNotBlank()) expression else defaultValue
            }
            is PsiReferenceExpression -> {
                val expression = extractExpression(pathVariableValue)
                if (expression.isNotBlank()) expression else defaultValue
            }
            else -> defaultValue
        }
    }

    companion object {
        private const val CONTROLLER_ANNOTATION = "io.micronaut.http.annotation.Controller"
        private const val ATTRIBUTE_NAME = "value"
        private const val PATH_VARIABLE_ANNOTATION = "io.micronaut.http.annotation.PathVariable"
    }
}