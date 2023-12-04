package com.nasller.requestmapper.annotations.extraction

import com.intellij.psi.PsiArrayInitializerMemberValue
import com.nasller.requestmapper.annotations.extraction.PsiExpressionExtractor.extractExpression
import com.nasller.requestmapper.utils.unquote

class PsiArrayInitializerMemberValueExtractor : PsiAnnotationValueExtractor<PsiArrayInitializerMemberValue> {

    override fun extract(value: PsiArrayInitializerMemberValue): List<String> = value.initializers.map {
        val element = extractExpression(it)
        when {
            element.isNotBlank() -> element
            it.text.isNotBlank() -> it.text.unquote()
            else -> ""
        }
    }
}