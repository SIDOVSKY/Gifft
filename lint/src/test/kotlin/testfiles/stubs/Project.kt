package testfiles.stubs

import com.android.tools.lint.checks.infrastructure.TestFiles.kt

object Project {
    val fragmentKey = arrayOf(
        AndroidX.fragment,

        kt(
            """
            package com.gifft.core.api.di
            
            import androidx.fragment.app.Fragment
            
            annotation class FragmentKey(val value: KClass<out Fragment>)
            """
        ).indented()
    )
}
