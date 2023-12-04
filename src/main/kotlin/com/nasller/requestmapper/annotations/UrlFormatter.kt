package com.nasller.requestmapper.annotations

interface UrlFormatter {
    fun format(classMapping: String, methodMapping: String): String
}