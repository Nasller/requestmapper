package com.viartemev.requestmapper.annotations.spring

import com.intellij.psi.PsiAnnotation
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
		val valueParam = psiAnnotation.findAttributeValue(METHOD_PARAM)
		if (valueParam != null && valueParam.text.isNotBlank() && "{}" != valueParam.text) {
			return valueParam.text.replace("RequestMethod.", "")
		}
		return MappingAnnotation.ANY_METHOD
	}

	private companion object {
		private const val METHOD_PARAM = "method"
	}
}