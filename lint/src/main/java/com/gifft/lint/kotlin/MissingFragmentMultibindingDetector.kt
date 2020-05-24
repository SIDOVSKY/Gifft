package com.gifft.lint.kotlin

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass

class MissingFragmentMultibindingDetector : Detector(), SourceCodeScanner {
    companion object {
        private const val FRAGMENT_KEY = "com.gifft.core.api.di.FragmentKey"
        private const val FRAGMENT = "androidx.fragment.app.Fragment"

        private const val DESCRIPTION =
            "Custom fragments must be multibound with a [$FRAGMENT_KEY] annotation"

        private val EXPLANATION = """
            We use dagger2 multibinding to create an all-knowing fragment factory
            which can inject dependencies into fragments' constructors.
            
            If reported fragment is not bound into a map of providers
            for [MultibindingFragmentFactory] then Android wont be able to create it
            and the app will be crashed during fragment initialization or recreation.
            
            To fix the issue, please, create a binding module
            
            @Module
            interface SubProjectCommonsModule {
                @Binds
                @IntoMap
                @FragmentKey(YOURFragment::class)
                fun YOURFragment.bindYOURFragment(): Fragment
            }
            
            and include it into CommonsComponent
            
            @Component(modules = [SubProjectCommonsModule::class])
            interface CommonsComponent
        """.trimIndent()

        val ISSUE = Issue.create(
            "MissingFragmentMultibinding",
            DESCRIPTION,
            EXPLANATION,
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(
                MissingFragmentMultibindingDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    private val fragmentDeclarations = mutableMapOf<String, Location>()
    private val keyedFragments = mutableSetOf<String>()

    override fun applicableSuperClasses() = listOf(FRAGMENT)

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (declaration.qualifiedName == FRAGMENT)
            return

        if ((declaration.sourcePsi as KtClass).isAbstract())
            return

        val className = declaration.name
        val extendsFragment = context.evaluator.extendsClass(declaration, FRAGMENT, true)

        if (className != null && extendsFragment) {
            fragmentDeclarations[className] = context.getNameLocation(declaration)
        }
    }

    // Using UastHandler for annotation processing because
    // visitAnnotationUsage is not being called for an unknown reason
    override fun getApplicableUastTypes() = listOf(UAnnotation::class.java)
    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {

        override fun visitAnnotation(node: UAnnotation) {
            if (node.qualifiedName == FRAGMENT_KEY) {
                val annotationArgument =
                    (node.sourcePsi as KtAnnotationEntry).valueArguments[0] as KtValueArgument
                val fragmentClassReference =
                    (annotationArgument.firstChild as KtClassLiteralExpression).firstChild.text

                keyedFragments.add(fragmentClassReference)
            }
        }
    }

    override fun afterCheckEachProject(context: Context) {
        super.afterCheckEachProject(context)

        fragmentDeclarations.forEach { (name, location) ->
            if (!keyedFragments.contains(name)) {
                context.report(ISSUE, location, DESCRIPTION)
            }
        }
    }
}
