package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PatchMappingSpek : Spek({
    describe("PatchMapping") {
        context("extractMethod") {
            it("should return PATCH") {
                val annotation = mock<PsiAnnotation> {}
                PatchMapping(annotation).extractMethod() shouldBeEqualTo "PATCH"
            }
        }
    }
})
