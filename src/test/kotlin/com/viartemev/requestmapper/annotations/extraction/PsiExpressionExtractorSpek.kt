package com.viartemev.requestmapper.annotations.extraction

import com.intellij.psi.*
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PsiExpressionExtractorSpek : Spek({
    describe("PsiExpressionExtractor") {
        context("extractExpression on PsiLiteralExpression") {
            it("should return unquoted text") {
                val psiLiteralExpression = mock<PsiLiteralExpression> {
                    on { text } doReturn "\"api\""
                }
                PsiExpressionExtractor.extractExpression(psiLiteralExpression) shouldBeEqualTo "api"
            }
        }
        context("extractExpression on PsiReferenceExpression") {
            it("should return unquoted text") {
                val psiElement = mock<PsiLiteralExpression> {
                    on { text } doReturn "\"api\""
                }
                val psiReferenceExpression = mock<PsiReferenceExpression> {
                    on { resolve() } doReturn it
                    on { children } doReturn arrayOf(psiElement)
                }
                PsiExpressionExtractor.extractExpression(psiReferenceExpression) shouldBeEqualTo "api"
            }
        }
        context("extractExpression on PsiReferenceExpression on not resolved value") {
            it("should return empty list") {
                val psiReferenceExpression = mock<PsiReferenceExpression> {
                    on { children } doReturn emptyArray<PsiExpression>()
                }
                PsiExpressionExtractor.extractExpression(psiReferenceExpression) shouldBeEqualTo ""
            }
        }
        context("extractExpression on PsiBinaryExpression") {
            it("should return sum of the left operator and the right operator") {
                val psiElement = mock<PsiLiteralExpression> {
                    on { text } doReturn "\"api\""
                }
                val psiBinaryExpression = mock<PsiBinaryExpression> {
                    on { lOperand } doReturn psiElement
                    on { rOperand } doReturn psiElement
                }
                PsiExpressionExtractor.extractExpression(psiBinaryExpression) shouldBeEqualTo "apiapi"
            }
        }
        context("extractExpression on PsiPolyadicExpression") {
            it("should return joined string of an each expression") {
                val psiElement = mock<PsiLiteralExpression> {
                    on { text } doReturn "\"api\""
                }
                val psiPolyadicExpression = mock<PsiPolyadicExpression> {
                    on { operands } doReturn arrayOf<PsiExpression>(psiElement, psiElement)
                }
                PsiExpressionExtractor.extractExpression(psiPolyadicExpression) shouldBeEqualTo "apiapi"
            }
        }
        context("extractExpression on others expressions") {
            it("should return empty string") {
                val psiAssignmentExpression = mock<PsiAssignmentExpression> {}
                PsiExpressionExtractor.extractExpression(psiAssignmentExpression) shouldBeEqualTo ""
            }
        }
    }
})
