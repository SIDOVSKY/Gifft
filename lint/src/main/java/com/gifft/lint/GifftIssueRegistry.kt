package com.gifft.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.gifft.lint.kotlin.MissingFragmentMultibindingDetector

class GifftIssueRegistry : IssueRegistry() {
    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API

    override val issues = listOf(
        MissingFragmentMultibindingDetector.ISSUE
    )
}
