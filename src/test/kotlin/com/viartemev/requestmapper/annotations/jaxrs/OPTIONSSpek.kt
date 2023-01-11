package com.viartemev.requestmapper.annotations.jaxrs

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object OPTIONSSpek : Spek({
    describe("OPTIONS") {
        context("extractMethod") {
            it("should return OPTIONS") {
                val annotation = mock<PsiAnnotation> {}
                OPTIONS(annotation).extractMethod() shouldBeEqualTo "OPTIONS"
            }
        }
    }
})
