package com.skaggsm.compose.lenses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.StateObject
import arrow.core.*
import arrow.optics.Prism

fun <T, U> MutableState<T>.get(prism: Prism<T, U>): MutableState<Either<T, U>> = PrismMutableState(this, prism)

internal class PrismMutableState<T, U>(
    private val state: MutableState<T>,
    private val prism: Prism<T, U>,
    private val derived: State<Either<T, U>> = derivedStateOf { prism.getOrModify(state.value) }
) : MutableState<Either<T, U>>, StateObject by (derived as StateObject), State<Either<T, U>> by derived {
    override var value: Either<T, U>
        get() = derived.value
        set(value) {
            when (value) {
                is Either.Right -> {
                    state.value = prism.reverseGet(value.value)
                }
                else -> return
            }
        }

    override fun component1(): Either<T, U> = value

    override fun component2(): (Either<T, U>) -> Unit = { value = it }
}

fun <T, U> MutableState<T>.reverseGet(prism: Prism<U, T>): MutableState<U> {
    /**
     * A state to track the output's state when the prism can't be applied to the input.
     *
     * This gets reset as soon as the input changes or the output can be applied to the input.
     */
    val alternate = mutableStateOf<Option<U>>(None)

    /**
     * A derived state that exploits the recomputation of the value to also reset the `alternate` state to [None].
     *
     * It is used to implement the [StateObject] methods by delegation since I don't want to yet ;)
     */
    val derived = derivedStateOf {
        alternate.value = None // When `this.value` changes, invalidate the "alternative"
        prism.reverseGet(this.value) // Get a value out of it to specify its dependency
    }

    return object : MutableState<U>, StateObject by (derived as StateObject), State<U> by derived {
        override var value: U
            get() {
                val der = derived.value // Run the derived function if inputs changed (resets the alternate value if so)
                return when (val alt = alternate.value) {
                    is Some -> {
                        alt.value // If there's an alternate value, use that
                    }
                    else -> {
                        der // Otherwise, fallback to computing it using `derived`
                    }
                }
            }
            set(value) {
                when (val result = prism.getOrModify(value)) {
                    is Either.Right -> {
                        // Prism applied successfully, propagate the change up to the input state.
                        this@reverseGet.value = result.value
                    }
                }
                // Save the input in `alternate` for if a call to `get` comes before any dependencies change.
                alternate.value = value.some()
            }

        override fun component1(): U = value

        override fun component2(): (U) -> Unit = { value = it }
    }
}
