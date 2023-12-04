package com.nasller.requestmapper.annotations.jaxrs

import com.intellij.psi.PsiAnnotation
import com.nasller.requestmapper.annotations.MappingAnnotation

class DELETE(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.DELETE_METHOD
}

class GET(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.GET_METHOD
}

class HEAD(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.HEAD_METHOD
}

class OPTIONS(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.OPTIONS_METHOD
}

class PATCH(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.PATCH_METHOD
}

class POST(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.POST_METHOD
}

class PUT(psiAnnotation: PsiAnnotation) : JaxRsMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.PUT_METHOD
}