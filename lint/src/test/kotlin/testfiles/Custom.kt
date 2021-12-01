package testfiles

import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import testfiles.stubs.AndroidX
import testfiles.stubs.Project

object Custom {
    private const val PACKAGE = "lint.test.custom"

    val fragment = arrayOf(
        AndroidX.fragment,

        kt(
            """
            package $PACKAGE

            import androidx.fragment.app.Fragment

            class TestFragment : Fragment()
            """
        ).indented()
    )

    val abstractFragment = arrayOf(
        AndroidX.fragment,

        kt(
            """
            package $PACKAGE

            import androidx.fragment.app.Fragment

            abstract class TestFragment : Fragment()
            """
        ).indented()
    )

    val fragmentMultibinding = arrayOf(
        AndroidX.fragment,
        *Project.fragmentKey,

        kt(
            """ 
            package $PACKAGE
            
            import androidx.fragment.app.Fragment
            import com.gifft.core.di.FragmentKey
            import dagger.Binds
            import dagger.Module
            import dagger.multibindings.IntoMap
            
            @Module
            interface TestCommonsModule {
                @Binds
                @IntoMap
                @FragmentKey(TestFragment::class)
                fun TestFragment.bindTestFragment(): Fragment
            }
            """
        ).indented()
    )
}
