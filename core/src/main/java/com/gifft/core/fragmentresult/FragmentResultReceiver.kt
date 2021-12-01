package com.gifft.core.fragmentresult

/**
 * Interface for passing result back to fragment-caller
 *
 * Using:
 * 1. For each result type declare a receiver interface
 *
 *    e. g.
 *    ```
 *    interface FragmentStringResultReceiver : FragmentResultReceiver<String>
 *    ````
 *    Due to type erasure.
 * 2. Implement interface on target fragment
 * 3. Start fragment for result like this:
 *    ```
 *    val requestCode = 123
 *    val newFragmentForResult = SomeFragment().apply {
 *        setTargetFragment(this@TargetFragment, requestCode)
 *    }
 *    ```
 * 4. Call target fragment like:
 *    ```
 *    (targetFragment as? FragmentStringResultReceiver)
 *        ?.onResult(targetRequestCode, "RESULT VALUE")
 *    ```
 */
interface FragmentResultReceiver<T> {

    /**
     * This method may be called when fragment view is destroyed
     */
    fun onResult(requestCode: Int, value: T)
}

interface FragmentStringResultReceiver : FragmentResultReceiver<String>
