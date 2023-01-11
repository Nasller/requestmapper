package com.viartemev.requestmapper.annotations.extraction

import com.intellij.psi.PsiAnnotationMemberValue
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PsiAnnotationMemberValueExtractorSpek : Spek({
    describe("PsiAnnotationMemberValueExtractor") {
        context("extract on PsiAnnotationMemberValue with blank text") {
            it("should return empty list") {
                val psiAnnotationMemberValue = mock<PsiAnnotationMemberValue> {
                    on { text } doReturn ""
                }
                PsiAnnotationMemberValueExtractor().extract(psiAnnotationMemberValue).shouldBeEmpty()
            }
        }
        context("extract on PsiAnnotationMemberValue with not blank text") {
            it("should return list with unquoted text") {
                val psiAnnotationMemberValue = mock<PsiAnnotationMemberValue> {
                    on { text } doReturn "\"api\""
                }
                val extract = PsiAnnotationMemberValueExtractor().extract(psiAnnotationMemberValue)
                extract.size shouldBeEqualTo 1
                extract shouldContain "api"
            }
        }
    }
})
