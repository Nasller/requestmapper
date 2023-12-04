package com.nasller.requestmapper.annotations.extraction

import com.intellij.psi.PsiAnnotationMemberValue
import com.nasller.requestmapper.annotations.extraction.PsiExpressionExtractor.extractExpression
import com.nasller.requestmapper.utils.unquote

class PsiAnnotationMemberValueExtractor : PsiAnnotationValueExtractor<PsiAnnotationMemberValue> {

    override fun extract(value: PsiAnnotationMemberValue): List<String> {
        val element = extractExpression(value)
        return when {
            element.isNotBlank() -> listOf(element)
            value.text.isNotBlank() -> listOf(value.text.unquote())
            else -> emptyList()
        }
    }
}