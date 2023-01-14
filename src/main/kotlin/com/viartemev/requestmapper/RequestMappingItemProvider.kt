package com.viartemev.requestmapper

import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor
import com.intellij.ide.util.gotoByName.*
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.MinusculeMatcher
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.util.Processor
import com.intellij.util.SynchronizedCollectConsumer
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.FindSymbolParameters

class RequestMappingItemProvider (context: PsiElement?) : DefaultChooseByNameItemProvider(context) {

    override fun filterElementsWithWeights(base: ChooseByNameViewModel, parameters: FindSymbolParameters, indicator: ProgressIndicator, consumer: Processor<in FoundItemDescriptor<*>?>): Boolean {
        return ProgressManager.getInstance().computePrioritized<Boolean, RuntimeException> { filter(base, parameters, indicator, consumer) }
    }

    companion object {
        private fun filter(base: ChooseByNameViewModel, parameters: FindSymbolParameters, indicator: ProgressIndicator, consumer: Processor<in FoundItemDescriptor<*>?>): Boolean {
            base.project?.putUserData(ChooseByNamePopup.CURRENT_SEARCH_PATTERN, parameters.completePattern)
            val namesList = getSortedResults(base, indicator, parameters)
            indicator.checkCanceled()
            return processByNames(base, indicator, consumer, namesList, parameters)
        }

        private fun getSortedResults(base: ChooseByNameViewModel, indicator: ProgressIndicator, parameters: FindSymbolParameters): List<FoundItemDescriptor<String>> {
            val pattern = parameters.completePattern
            if (pattern.isEmpty() && !base.canShowListForEmptyPattern()) {
                return emptyList()
            }
            val matcher = NameUtil.buildMatcher("*$pattern").build()
            val itemsList = ArrayList<FoundItemDescriptor<String>>()
            val collect = SynchronizedCollectConsumer(itemsList)
            (base.model as? ChooseByNameModelEx)?.let {
                indicator.checkCanceled()
                it.processNames({name ->
                    indicator.checkCanceled()
                    return@processNames matches(matcher, name, pattern)?.run {
                        collect.consume(this)
                        true
                    } ?: false
                }, parameters)
            }
            itemsList.sortWith(Comparator.comparing(FoundItemDescriptor<String>::getWeight).reversed())
            indicator.checkCanceled()
            return itemsList
        }

        private fun processByNames(base: ChooseByNameViewModel, indicator: ProgressIndicator, consumer: Processor<in FoundItemDescriptor<*>?>, itemsList: List<FoundItemDescriptor<String>>, parameters: FindSymbolParameters): Boolean {
            val model = base.model
            for (item in itemsList) {
                indicator.checkCanceled()
                val elements = if (model is ContributorsBasedGotoByModel) model.getElementsByName(item.item, parameters, indicator)
                else model.getElementsByName(item.item, parameters.isSearchInLibraries, parameters.completePattern)
                if (elements.size > 1) {
                    if (!ContainerUtil.process(elements.map { FoundItemDescriptor(it, 0) }, consumer)) return false
                } else if (elements.size == 1) {
                    if (!consumer.process(FoundItemDescriptor(elements[0], 0))) return false
                }
            }
            return true
        }

        private fun matches(matcher: MinusculeMatcher, name: String?, pattern: String): FoundItemDescriptor<String>? {
            if (name.isNullOrEmpty()) return null
            return try {
                if (pattern == "/") {
                    FoundItemDescriptor(name,0)
                } else {
                    if(matcher.matches(name)){
                        FoundItemDescriptor(name, matcher.matchingDegree(name))
                    } else null
                }
            } catch (_: Exception) {
                null // no matches appear valid result for "bad" pattern
            }
        }
    }
}