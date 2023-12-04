package com.nasller.requestmapper.annotations.extraction

import com.intellij.psi.*
import com.nasller.requestmapper.utils.unquote

object PsiExpressionExtractor {

    fun extractExpression(psiElement: PsiElement): String = when (psiElement) {
        is PsiLiteralExpression -> extractLiteralExpression(psiElement)
        is PsiBinaryExpression -> extractBinaryExpression(psiElement)
        is PsiPolyadicExpression -> extractPsiPolyadicExpression(psiElement)
        is PsiReferenceExpression -> extractPath(psiElement)
        else -> ""
    }

    private fun extractPath(value: PsiReferenceExpression): String {
        return value.resolve()?.let { psiElement: PsiElement ->
            psiElement
                .children
                .asSequence()
                .filter { it is PsiBinaryExpression || it is PsiLiteralExpression || it is PsiPolyadicExpression }
                .map { extractExpression(it) }
                .toList()
                // only one exists
                .firstOrNull()
        } ?: ""
    }

    private fun extractLiteralExpression(psiElement: PsiLiteralExpression): String {
        return if (psiElement.value == null) {
            psiElement.text.unquote()
        } else psiElement.value.toString()
    }

    /** rOperand always presents in static final variables */
    private fun extractBinaryExpression(psiElement: PsiBinaryExpression) =
        extractExpression(psiElement.lOperand) + extractExpression(psiElement.rOperand!!)

    private fun extractPsiPolyadicExpression(psiElement: PsiPolyadicExpression) =
        psiElement
            .operands
            .joinToString(separator = "", transform = { extractExpression(it) })
}