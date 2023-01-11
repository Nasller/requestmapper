package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PostMappingSpek : Spek({
    describe("PostMapping") {
        context("extractMethod") {
            it("should return POST") {
                val annotation = mock<PsiAnnotation> {}
                PostMapping(annotation).extractMethod() shouldBeEqualTo "POST"
            }
        }
    }
})
