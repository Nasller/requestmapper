package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GetMappingSpek : Spek({
    describe("GetMapping") {
        context("extractMethod") {
            it("should return GET") {
                val annotation = mock<PsiAnnotation> {}
                GetMapping(annotation).extractMethod() shouldBeEqualTo "GET"
            }
        }
    }
})
