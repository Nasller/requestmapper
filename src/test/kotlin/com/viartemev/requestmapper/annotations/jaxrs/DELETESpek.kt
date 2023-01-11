package com.viartemev.requestmapper.annotations.jaxrs

import com.intellij.psi.PsiAnnotation
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DELETESpek : Spek({
    describe("DELETE") {
        context("extractMethod") {
            it("should return DELETE") {
                val annotation = mock<PsiAnnotation> {}
                DELETE(annotation).extractMethod() shouldBeEqualTo "DELETE"
            }
        }
    }
})
