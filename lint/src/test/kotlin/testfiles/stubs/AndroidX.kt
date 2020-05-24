package testfiles.stubs

import com.android.tools.lint.checks.infrastructure.TestFiles.kt

object AndroidX {
    val fragment = kt(
        """
            package androidx.fragment.app
            class Fragment
            """
    ).indented()
}
