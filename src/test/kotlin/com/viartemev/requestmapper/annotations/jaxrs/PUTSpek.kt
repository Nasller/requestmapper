package com.viartemev.requestmapper.annotations.jaxrs

import com.intellij.psi.PsiAnnotation
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PUTSpek : Spek({
    describe("PUT") {
        context("extractMethod") {
            it("should return PUT") {
                val annotation = mock<PsiAnnotation> {}
                PUT(annotation).extractMethod() shouldBeEqualTo "PUT"
            }
        }
    }
})
