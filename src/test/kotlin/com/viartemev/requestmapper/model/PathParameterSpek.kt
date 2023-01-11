package com.viartemev.requestmapper.model

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiModifierList
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiType
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PathParameterSpek : Spek({

    describe("PathParameter") {
        context("extractParameterNameWithType on PsiParameter without annotations") {
            it("should return null") {
                val annotationsList = mock<PsiModifierList> {
                    on { annotations } doReturn emptyArray<PsiAnnotation>()
                }
                val psiType = mock<PsiType> {
                    on { presentableText } doReturn "variable"
                }
                val mock = mock<PsiParameter> {
                    on { modifierList } doReturn annotationsList
                    on { type } doReturn psiType
                }
                PathParameter(mock).extractParameterNameWithType("PathVariable", { _: PsiAnnotation, _: String -> "42" }).shouldBeNull()
            }
        }
        context("extractParameterNameWithType on PsiParameter with PathParam annotation") {
            it("should return pair annotation name-type") {
                val annotationName = "PathVariable"
                val pathAnnotation = mock<PsiAnnotation> {
                    on { qualifiedName } doReturn annotationName
                }
                val annotationsList = mock<PsiModifierList> {
                    on { annotations } doReturn arrayOf(pathAnnotation)
                }
                val psiType = mock<PsiType> {
                    on { presentableText } doReturn "long"
                }
                val mock = mock<PsiParameter> {
                    on { modifierList } doReturn annotationsList
                    on { type } doReturn psiType
                    on { name } doReturn "Long"
                }
                PathParameter(mock).extractParameterNameWithType(annotationName) { _: PsiAnnotation, _: String -> "id" } shouldBeEqualTo Pair("id", "long")
            }
        }
    }
})
