package com.gifft.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.gifft.lint.kotlin.MissingFragmentMultibindingDetector

@Suppress("unused", "UnstableApiUsage")
class GifftIssueRegistry : IssueRegistry() {
    override val api: Int = CURRENT_API

    override val issues = listOf(
        MissingFragmentMultibindingDetector.ISSUE,
    )

    override val vendor: Vendor = Vendor(
        vendorName = "Gifft",
        feedbackUrl = "https://github.com/SIDOVSKY/Gifft/issues",
        contact = "https://github.com/SIDOVSKY/Gifft"
    )
}
