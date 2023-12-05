package com.nasller.requestmapper.annotations.spring.classmapping

import com.intellij.psi.PsiAnnotation

class ClassRequestMapping(private val psiAnnotation: PsiAnnotation) : SpringClassMappingAnnotation {
    override fun fetchClassMapping(): List<ClassMappingData> =
        SpringClassMappingAnnotation.fetchPathValueMapping(psiAnnotation).map { ClassMappingData("", it) }
}