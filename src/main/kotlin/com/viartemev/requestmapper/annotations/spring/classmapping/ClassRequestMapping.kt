package com.viartemev.requestmapper.annotations.spring.classmapping

import com.intellij.psi.PsiAnnotation

class ClassRequestMapping(val psiAnnotation: PsiAnnotation) : SpringClassMappingAnnotation {
    override fun fetchClassMapping(): List<ClassMappingData> =
        SpringClassMappingAnnotation.fetchPathValueMapping(psiAnnotation).map { ClassMappingData("", it) }
}