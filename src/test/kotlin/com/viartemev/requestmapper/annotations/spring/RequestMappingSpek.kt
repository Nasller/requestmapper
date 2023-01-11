package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.*
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RequestMappingSpek : Spek({

    describe("RequestMapping") {
        context("values on annotation without anything") {
            val psiParameterList = mock<PsiParameterList> {
                on { parameters } doReturn emptyArray<PsiParameter>()
            }
            val psiMethod = mock<PsiMethod> {
                on { parameterList } doReturn psiParameterList
            }
            val annotation = mock<PsiAnnotation> {
                on { parent } doReturn psiMethod
            }
            val requestMapping = RequestMapping(annotation)
            it("should return one root mapping with default GET method") {
                val values = requestMapping.values()
                values.size shouldBeEqualTo 1
                values[0].name shouldBeEqualTo "GET /"
            }
        }

        context("values on annotation with annotated class") {
            val psiParameterList = mock<PsiParameterList> {
                on { parameters } doReturn emptyArray<PsiParameter>()
            }
            val memberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "api"
            }
            val mappingAnnotation = mock<PsiAnnotation> {
                on { qualifiedName } doReturn "org.springframework.web.bind.annotation.RequestMapping"
                on { findAttributeValue("path") } doReturn memberValue
            }
            val psiModifierList = mock<PsiModifierList> {
                on { annotations } doReturn arrayOf(mappingAnnotation)
            }
            val clazz = mock<PsiClass> {
                on { modifierList } doReturn psiModifierList
            }
            val psiMethod = mock<PsiMethod> {
                on { parameterList } doReturn psiParameterList
                on { containingClass } doReturn clazz
            }
            val annotation = mock<PsiAnnotation> {
                on { parent } doReturn psiMethod
            }
            val requestMapping = RequestMapping(annotation)
            it("should return one class mapping with default GET method") {
                val values = requestMapping.values()
                values.size shouldBeEqualTo 1
                values[0].name shouldBeEqualTo "GET /api"
            }
        }

        context("values on annotation with annotated class and method") {
            val psiParameterList = mock<PsiParameterList> {
                on { parameters } doReturn emptyArray<PsiParameter>()
            }
            val classMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "api"
            }
            val classMappingAnnotation = mock<PsiAnnotation> {
                on { qualifiedName } doReturn "org.springframework.web.bind.annotation.RequestMapping"
                on { findAttributeValue("path") } doReturn classMappingMemberValue
            }
            val psiModifierList = mock<PsiModifierList> {
                on { annotations } doReturn arrayOf(classMappingAnnotation)
            }
            val clazz = mock<PsiClass> {
                on { modifierList } doReturn psiModifierList
            }
            val psiMethod = mock<PsiMethod> {
                on { parameterList } doReturn psiParameterList
                on { containingClass } doReturn clazz
            }
            val methodMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "method"
            }
            val methodMappingAnnotation = mock<PsiAnnotation> {
                on { parent } doReturn psiMethod
                on { findAttributeValue("path") } doReturn methodMappingMemberValue
            }
            val requestMapping = RequestMapping(methodMappingAnnotation)
            it("should return one mapping with default GET method and path which contains class and method mapping") {
                val values = requestMapping.values()
                values.size shouldBeEqualTo 1
                values[0].name shouldBeEqualTo "GET /api/method"
            }
        }

        context("values on annotation with annotated class and method with path variable without type") {
            val psiParameterList = mock<PsiParameterList> {
                on { parameters } doReturn emptyArray<PsiParameter>()
            }
            val classMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "api"
            }
            val classMappingAnnotation = mock<PsiAnnotation> {
                on { qualifiedName } doReturn "org.springframework.web.bind.annotation.RequestMapping"
                on { findAttributeValue("path") } doReturn classMappingMemberValue
            }
            val psiModifierList = mock<PsiModifierList> {
                on { annotations } doReturn arrayOf(classMappingAnnotation)
            }
            val clazz = mock<PsiClass> {
                on { modifierList } doReturn psiModifierList
            }
            val psiMethod = mock<PsiMethod> {
                on { parameterList } doReturn psiParameterList
                on { containingClass } doReturn clazz
            }
            val methodMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "{id}"
            }
            val methodMappingAnnotation = mock<PsiAnnotation> {
                on { parent } doReturn psiMethod
                on { findAttributeValue("path") } doReturn methodMappingMemberValue
            }
            val requestMapping = RequestMapping(methodMappingAnnotation)
            it("should return one mapping with default GET method and path which contains class and method mapping") {
                val values = requestMapping.values()
                values.size shouldBeEqualTo 1
                values[0].name shouldBeEqualTo "GET /api/{Object:id}"
            }
        }
        context("values on annotation with annotated class and method with path variable with Long type") {
            val methodModifierMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "id"
            }
            val methodModifierAnnotation = mock<PsiAnnotation> {
                on { qualifiedName } doReturn "org.springframework.web.bind.annotation.PathVariable"
                on { findAttributeValue("value") } doReturn methodModifierMappingMemberValue
            }
            val methodModifierList = mock<PsiModifierList> {
                on { annotations } doReturn arrayOf(methodModifierAnnotation)
            }
            val parameterType = mock<PsiType> {
                on { presentableText } doReturn "Long"
            }
            val psiParameter = mock<PsiParameter> {
                on { type } doReturn parameterType
                on { modifierList } doReturn methodModifierList
                on { name } doReturn "String"
            }
            val psiParameterList = mock<PsiParameterList> {
                on { parameters } doReturn arrayOf(psiParameter)
            }
            val classMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "api"
            }
            val classMappingAnnotation = mock<PsiAnnotation> {
                on { qualifiedName } doReturn "org.springframework.web.bind.annotation.RequestMapping"
                on { findAttributeValue("path") } doReturn classMappingMemberValue
            }
            val classPsiModifierList = mock<PsiModifierList> {
                on { annotations } doReturn arrayOf(classMappingAnnotation)
            }
            val clazz = mock<PsiClass> {
                on { modifierList } doReturn classPsiModifierList
            }
            val psiMethod = mock<PsiMethod> {
                on { parameterList } doReturn psiParameterList
                on { containingClass } doReturn clazz
            }
            val methodMappingMemberValue = mock<PsiLiteralExpression> {
                on { text } doReturn "{id}"
            }
            val methodPsiAnnotation = mock<PsiAnnotation> {
                on { parent } doReturn psiMethod
                on { findAttributeValue("path") } doReturn methodMappingMemberValue
            }
            val methodMappingAnnotation = RequestMapping(methodPsiAnnotation)
            it("should return one mapping with default GET method and path which contains class and method mapping") {
                val values = methodMappingAnnotation.values()
                values.size shouldBeEqualTo 1
                values[0].name shouldBeEqualTo "GET /api/{Long:id}"
            }
        }

        context("values on an annotation with annotated class with an array of paths") {
            val psiParameterList = mock<PsiParameterList> {
                on { parameters } doReturn emptyArray<PsiParameter>()
            }
            val annotatedMemberValue1 = mock<PsiLiteralExpression> {
                on { text } doReturn "api/v2"
            }
            val annotatedMemberValue2 = mock<PsiLiteralExpression> {
                on { text } doReturn "api"
            }
            val memberValue = mock<PsiArrayInitializerMemberValue> {
                on { initializers } doReturn arrayOf(annotatedMemberValue1, annotatedMemberValue2)
            }
            val mappingAnnotation = mock<PsiAnnotation> {
                on { qualifiedName } doReturn "org.springframework.web.bind.annotation.RequestMapping"
                on { findAttributeValue("path") } doReturn memberValue
            }
            val psiModifierList = mock<PsiModifierList> {
                on { annotations } doReturn arrayOf(mappingAnnotation)
            }
            val clazz = mock<PsiClass> {
                on { modifierList } doReturn psiModifierList
            }
            val psiMethod = mock<PsiMethod> {
                on { parameterList } doReturn psiParameterList
                on { containingClass } doReturn clazz
            }
            val annotation = mock<PsiAnnotation> {
                on { parent } doReturn psiMethod
            }
            val requestMapping = RequestMapping(annotation)
            it("should return one class mapping with default GET method") {
                val values = requestMapping.values()
                values.size shouldBeEqualTo 2
                values.map { it.name } shouldContainAll listOf("GET /api", "GET /api/v2")
            }
        }
    }
})
