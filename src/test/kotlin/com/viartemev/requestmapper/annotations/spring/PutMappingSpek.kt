package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PutMappingSpek : Spek({
    describe("PutMapping") {
        context("extractMethod") {
            it("should return PUT") {
                val annotation = mock<PsiAnnotation> {}
                PutMapping(annotation).extractMethod() shouldBeEqualTo "PUT"
            }
        }
    }
})
