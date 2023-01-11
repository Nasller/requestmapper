package com.viartemev.requestmapper.annotations.extraction

import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiPolyadicExpression
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PsiPolyadicExpressionExtractorSpek : Spek({
    describe("PsiPolyadicExpressionExtractor") {
        context("extract with 2 PsiLiteralExpressions") {
            it("should return list with sum of literal expressions texts") {
                val apiElement = mock<PsiLiteralExpression> {
                    on { text } doReturn "\"/api\""
                }
                val versionElement = mock<PsiLiteralExpression> {
                    on { text } doReturn "\"/v1\""
                }
                val psiPolyadicExpression = mock<PsiPolyadicExpression> {
                    on { operands } doReturn arrayOf(apiElement, versionElement)
                }
                val extract = PsiPolyadicExpressionExtractor().extract(psiPolyadicExpression)
                extract.size shouldBeEqualTo 1
                extract shouldContain "/api/v1"
            }
        }
    }
})
