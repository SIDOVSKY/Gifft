package com.gifft.core.api

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

const val FRAGMENT_NAV_PARAM_KEY = "FragmentNavigationParameter"

/**
 * Usage:
 * ```
 * parentFragmentManager.commit {
 *     replace<SomeFragment>(id, tag, SomeParcelable().toNavBundle())
 * }
 * ```
 */
fun Parcelable.toNavBundle(existingBundle: Bundle = Bundle()): Bundle =
    existingBundle.apply {
        putParcelable(FRAGMENT_NAV_PARAM_KEY, this@toNavBundle)
    }

/**
 * Nav param should be put to fragment arguments using [toNavBundle]
  */
fun <T : Parcelable> Fragment.getNavParam(): T? =
    arguments?.getParcelable<T>(FRAGMENT_NAV_PARAM_KEY)

/**
 * Nav param should be put to fragment arguments using [toNavBundle]
 */
fun <T : Parcelable> Fragment.requireNavParam(): T =
    getNavParam()
        ?: throw IllegalStateException("Fragment $this does not have navigation parameters.")
