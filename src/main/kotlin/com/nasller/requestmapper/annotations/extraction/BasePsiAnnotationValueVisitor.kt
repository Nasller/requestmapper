package com.nasller.requestmapper.annotations.extraction

import com.intellij.psi.*

abstract class BasePsiAnnotationValueVisitor : PsiAnnotationValueVisitor {

    fun visit(annotation: PsiAnnotation, parameter: String): List<String> {
        return when (val attributeValue = annotation.findAttributeValue(parameter)) {
            is PsiArrayInitializerMemberValue -> visitPsiArrayInitializerMemberValue(attributeValue)
            is PsiReferenceExpression -> visitPsiReferenceExpression(attributeValue)
            is PsiBinaryExpression -> visitPsiBinaryExpression(attributeValue)
            is PsiPolyadicExpression -> visitPsiPolyadicExpression(attributeValue)
            else -> if (attributeValue != null && attributeValue.text.isNotBlank()) visitPsiAnnotationMemberValue(attributeValue) else emptyList()
        }
    }
}