package com.viartemev.requestmapper.annotations

import com.intellij.psi.*
import com.viartemev.requestmapper.annotations.extraction.*

class PathAnnotation(private val annotation: PsiAnnotation) {
    fun fetchMappings(parameter: String): List<String> {
        return object : BasePsiAnnotationValueVisitor() {
            override fun visitPsiArrayInitializerMemberValue(arrayAValue: PsiArrayInitializerMemberValue) =
                PsiArrayInitializerMemberValueExtractor().extract(arrayAValue)

            override fun visitPsiReferenceExpression(expression: PsiReferenceExpression) =
                PsiReferenceExpressionExtractor().extract(expression)

            override fun visitPsiAnnotationMemberValue(value: PsiAnnotationMemberValue) =
                PsiAnnotationMemberValueExtractor().extract(value)

            override fun visitPsiBinaryExpression(expression: PsiBinaryExpression) =
                PsiBinaryExpressionExtractor().extract(expression)

            override fun visitPsiPolyadicExpression(expression: PsiPolyadicExpression) =
                PsiPolyadicExpressionExtractor().extract(expression)
        }.visit(annotation, parameter)
    }
}