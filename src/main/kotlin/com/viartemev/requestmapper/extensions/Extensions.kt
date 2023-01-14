package com.viartemev.requestmapper.extensions

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.openapi.extensions.ExtensionPointName

object Extensions {

    private const val EXTENSION_POINT_NAME = "com.viartemev.requestmapper.requestMappingContributor"
    private val extensionPoints = ExtensionPointName.create<ChooseByNameContributorEx>(EXTENSION_POINT_NAME)

    fun getExtensions(): List<ChooseByNameContributorEx> {
        return extensionPoints.extensionList
    }
}
