package com.viartemev.requestmapper.annotations.spring

import com.viartemev.requestmapper.annotations.UrlFormatter
import com.viartemev.requestmapper.utils.dropFirstEmptyStringIfExists

object SpringUrlFormatter : UrlFormatter {
    override fun format(classMapping: String, methodMapping: String): String {
        val classPathSeq = classMapping.splitToSequence('/').filterNot { it.isBlank() }
        val methodPathList = methodMapping.split('/').dropFirstEmptyStringIfExists()
        return (classPathSeq + methodPathList).joinToString(separator = "/", prefix = "/").replace("\${", "{")
    }
}