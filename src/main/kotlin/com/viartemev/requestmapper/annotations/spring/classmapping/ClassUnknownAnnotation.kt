package com.viartemev.requestmapper.annotations.spring.classmapping

class ClassUnknownAnnotation : SpringClassMappingAnnotation {
    override fun fetchClassMapping(): List<ClassMappingData> = emptyList()
}