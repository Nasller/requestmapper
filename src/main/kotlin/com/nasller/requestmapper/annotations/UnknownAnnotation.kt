package com.nasller.requestmapper.annotations

import com.nasller.requestmapper.RequestMappingItem

object UnknownAnnotation : MappingAnnotation {
    override fun values(): List<RequestMappingItem> = emptyList()
}