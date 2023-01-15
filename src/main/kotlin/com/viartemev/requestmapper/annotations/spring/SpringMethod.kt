package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiQualifiedReference
import com.viartemev.requestmapper.annotations.MappingAnnotation

class DeleteMapping(psiAnnotation: PsiAnnotation) : RequestMapping(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.DELETE_METHOD
}

class GetMapping(psiAnnotation: PsiAnnotation) : RequestMapping(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.GET_METHOD
}

class PatchMapping(psiAnnotation: PsiAnnotation) : RequestMapping(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.PATCH_METHOD
}

class PostMapping(psiAnnotation: PsiAnnotation) : RequestMapping(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.POST_METHOD
}

class PutMapping(psiAnnotation: PsiAnnotation) : RequestMapping(psiAnnotation) {
	override fun extractMethod() = MappingAnnotation.PUT_METHOD
}

open class RequestMapping(psiAnnotation: PsiAnnotation) : SpringMappingAnnotation(psiAnnotation) {
	override fun extractMethod(): String {
		val children = psiAnnotation.findAttributeValue(METHOD_PARAM)?.children
		if (!children.isNullOrEmpty()) {
            return if(children[0] is PsiJavaToken) children.filterIsInstance<PsiQualifiedReference>().map { it.referenceName }
                .filter { it.isNullOrBlank().not() && it != "RequestMethod" }
                .joinToString(" ").ifBlank { MappingAnnotation.ANY_METHOD }
			else children.filterIsInstance<PsiIdentifier>().joinToString(" ") { it.text }
		}
		return MappingAnnotation.ANY_METHOD
	}

	private companion object {
		private const val METHOD_PARAM = "method"
	}
}