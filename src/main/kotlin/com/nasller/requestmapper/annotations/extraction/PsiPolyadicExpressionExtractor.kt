package com.nasller.requestmapper.annotations.extraction

import com.intellij.psi.PsiPolyadicExpression
import com.nasller.requestmapper.annotations.extraction.PsiExpressionExtractor.extractExpression

class PsiPolyadicExpressionExtractor : PsiAnnotationValueExtractor<PsiPolyadicExpression> {

    override fun extract(value: PsiPolyadicExpression) = listOf(extractExpression(value))
}