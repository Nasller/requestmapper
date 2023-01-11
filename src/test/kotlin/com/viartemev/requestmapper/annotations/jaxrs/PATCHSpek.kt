package com.viartemev.requestmapper.annotations.jaxrs

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PATCHSpek : Spek({
    describe("PATCH") {
        context("extractMethod") {
            it("should return PATCH") {
                val annotation = mock<PsiAnnotation> {}
                PATCH(annotation).extractMethod() shouldBeEqualTo "PATCH"
            }
        }
    }
})
