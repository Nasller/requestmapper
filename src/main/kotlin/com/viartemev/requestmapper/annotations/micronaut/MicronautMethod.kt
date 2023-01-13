package com.viartemev.requestmapper.annotations.micronaut

import com.intellij.psi.PsiAnnotation
import com.viartemev.requestmapper.annotations.MappingAnnotation

class Delete(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.DELETE_METHOD
}

class Get(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.GET_METHOD
}

class Head(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.HEAD_METHOD
}

class Options(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.OPTIONS_METHOD
}

class Patch(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.PATCH_METHOD
}

class Post(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.POST_METHOD
}

class Put(psiAnnotation: PsiAnnotation) : MicronautMappingAnnotation(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.PUT_METHOD
}
