package com.nasller.requestmapper.annotations.extraction

import com.intellij.psi.*

interface PsiAnnotationValueVisitor {

    fun visitPsiArrayInitializerMemberValue(arrayAValue: PsiArrayInitializerMemberValue): List<String>

    fun visitPsiReferenceExpression(expression: PsiReferenceExpression): List<String>

    fun visitPsiAnnotationMemberValue(value: PsiAnnotationMemberValue): List<String>

    fun visitPsiBinaryExpression(expression: PsiBinaryExpression): List<String>

    fun visitPsiPolyadicExpression(expression: PsiPolyadicExpression): List<String>
}