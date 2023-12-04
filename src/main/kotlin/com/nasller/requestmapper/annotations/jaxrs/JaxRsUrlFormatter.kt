package com.nasller.requestmapper.annotations.jaxrs

import com.nasller.requestmapper.annotations.UrlFormatter
import com.nasller.requestmapper.utils.dropFirstEmptyStringIfExists

object JaxRsUrlFormatter : UrlFormatter {
    override fun format(classMapping: String, methodMapping: String): String {
        val classPathSeq = classMapping.splitToSequence('/').filterNot { it.isBlank() }
        val methodPathList = methodMapping.split('/').dropFirstEmptyStringIfExists()
        return (classPathSeq + methodPathList).joinToString(separator = "/", prefix = "/")
    }
}