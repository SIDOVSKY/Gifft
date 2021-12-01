package com.gifft.core.recycler

import androidx.recyclerview.widget.DiffUtil

/**
 * Usage:
 * ```
 * SequenceDiffCallback<YourClass>(
 *     identityYields = {
 *         yield(it.id)
 *     },
 *     contentYields = {
 *         yield(it.sender)
 *         yield(it.date)
 *         yield(it.text)
 *     }
 * )
 * ```
 */
class SequenceDiffCallback<T>(
    private val identityYields: suspend SequenceScope<Any>.(T) -> Unit,
    private val contentYields: suspend SequenceScope<Any>.(T) -> Unit
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) =
        sequenceEquals(
            sequence { identityYields(oldItem) },
            sequence { identityYields(newItem) }
        )

    override fun areContentsTheSame(oldItem: T, newItem: T) =
        sequenceEquals(
            sequence { contentYields(oldItem) },
            sequence { contentYields(newItem) }
        )

    private fun <T> sequenceEquals(
        sequence1: Sequence<T>,
        sequence2: Sequence<T>
    ): Boolean {
        val iterator1 = sequence1.iterator()
        val iterator2 = sequence2.iterator()

        while (iterator1.hasNext() && iterator2.hasNext()) {
            if (iterator1.next() != iterator2.next())
                return false
        }

        return !iterator1.hasNext() && !iterator2.hasNext()
    }
}
