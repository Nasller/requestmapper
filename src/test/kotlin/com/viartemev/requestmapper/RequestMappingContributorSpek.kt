package com.viartemev.requestmapper

import com.intellij.ide.util.gotoByName.LanguageRef
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.command.impl.DummyProject
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.viartemev.requestmapper.contributors.RequestMappingByNameContributor
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RequestMappingContributorSpek : Spek({

    describe("RequestMappingContributor") {
        context("getItemsByName on empty navigationItems list") {
            it("should return empty list") {
                val contributor = object : RequestMappingByNameContributor() {
                    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> = emptySequence()
                    override fun getLanguageRef(): LanguageRef = LanguageRef.forLanguage(JavaLanguage.INSTANCE)
                }
                contributor.getItemsByName("name", "pattern", DummyProject.getInstance(), false).size shouldBeEqualTo 0
            }
        }
        context("getItemsByName with 2 mapping items") {
            it("should return item a particular name") {
                val psiElement = mock<PsiElement> {}
                val navigationItems = listOf(
                    RequestMappingItem(psiElement, "", "/api/v1/users", "GET"),
                    RequestMappingItem(psiElement, "", "/api/v2/users", "GET")
                )
                val contributor = object : RequestMappingByNameContributor(navigationItems) {
                    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> = emptySequence()
                    override fun getLanguageRef(): LanguageRef = LanguageRef.forLanguage(JavaLanguage.INSTANCE)
                }
                val itemsByName = contributor.getItemsByName("GET /api/v1/users", "pattern", DummyProject.getInstance(), false)
                itemsByName.size shouldBeEqualTo 1
                itemsByName[0].name shouldBeEqualTo "GET /api/v1/users"
            }
        }
        context("getNames on empty navigationItems list") {
            it("should return empty list") {
                val contributor = object : RequestMappingByNameContributor() {
                    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> = emptySequence()
                    override fun getLanguageRef(): LanguageRef = LanguageRef.forLanguage(JavaLanguage.INSTANCE)
                }
                contributor.getNames(DummyProject.getInstance(), false).size shouldBeEqualTo 0
            }
        }
        context("getNames on not method annotations") {
            it("should return empty list") {
                val annotationParent = mock<PsiElement> {}
                val psiAnnotation = mock<PsiAnnotation> {
                    on { parent } doReturn annotationParent
                }
                val contributor = object : RequestMappingByNameContributor() {
                    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> = sequenceOf(psiAnnotation)
                    override fun getLanguageRef(): LanguageRef = LanguageRef.forLanguage(JavaLanguage.INSTANCE)
                }
                contributor.getNames(DummyProject.getInstance(), false).size shouldBeEqualTo 0
            }
        }
        context("getNames with one RequestMapping annotation") {
            it("should return one name of mapping") {
                val psiParameterList = mock<PsiParameterList> {
                    on { parameters } doReturn emptyArray<PsiParameter>()
                }
                val memberValue = mock<PsiAnnotationMemberValue> {
                    on { text } doReturn "api"
                }
                val mappingAnnotation = mock<PsiAnnotation> {
                    on { qualifiedName } doReturn "org.springframework.web.bind.annotation.RequestMapping"
                    on { findAttributeValue("path") } doReturn memberValue
                }
                val psiModifierList = mock<PsiModifierList> {
                    on { annotations } doReturn arrayOf(mappingAnnotation)
                }
                val clazz = mock<PsiClass> {
                    on { modifierList } doReturn psiModifierList
                }
                val psiMethod = mock<PsiMethod> {
                    on { parameterList } doReturn psiParameterList
                    on { containingClass } doReturn clazz
                }
                val annotation = mock<PsiAnnotation> {
                    on { parent } doReturn psiMethod
                }
                val annotationSearcher: (String, Project) -> Sequence<PsiAnnotation> = { name: String, _ -> if (name == "RequestMapping") sequenceOf(annotation) else emptySequence() }
                val contributor = object : RequestMappingByNameContributor() {
                    override fun getAnnotationSearchers(annotationName: String, scope: GlobalSearchScope): Sequence<PsiAnnotation> = annotationSearcher(annotationName, scope.project!!)
                    override fun getLanguageRef(): LanguageRef = LanguageRef.forLanguage(JavaLanguage.INSTANCE)
                }
                val names = contributor.getNames(DummyProject.getInstance(), false)
                names.size shouldBeEqualTo 1
                names[0] shouldBeEqualTo "GET /api"
            }
        }
    }
})