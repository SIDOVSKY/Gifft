import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.gifft.lint.kotlin.MissingFragmentMultibindingDetector
import org.junit.Test
import testfiles.Custom

@Suppress("UnstableApiUsage")
class MissingFragmentMultibindingDetectorTest {

    @Test
    fun `should accept multibound fragment`() {
        lint()
            .allowMissingSdk()
            .files(
                *Custom.fragment,
                *Custom.fragmentMultibinding
            )
            .issues(MissingFragmentMultibindingDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `should skip abstract fragment without multibinding`() {
        lint()
            .allowMissingSdk()
            .files(
                *Custom.abstractFragment
            )
            .issues(MissingFragmentMultibindingDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `should detect fragment with forgotten multibinding`() {
        lint()
            .allowMissingSdk()
            .files(
                *Custom.fragment
            )
            .issues(MissingFragmentMultibindingDetector.ISSUE)
            .run()
            .expectErrorCount(1)
    }
}
