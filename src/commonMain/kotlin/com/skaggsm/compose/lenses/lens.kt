package com.skaggsm.compose.lenses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshots.StateObject
import arrow.optics.Lens

fun <T, U> MutableState<T>.get(lens: Lens<T, U>): MutableState<U> = LensMutableState(this, lens)

internal class LensMutableState<T, U>(
    private val state: MutableState<T>,
    private val lens: Lens<T, U>,
    private val derived: State<U> = derivedStateOf { lens.get(state.value) }
) : MutableState<U>, StateObject by (derived as StateObject), State<U> by derived {
    override var value: U
        get() = derived.value
        set(value) {
            state.value = lens.set(state.value, value)
        }

    override fun component1(): U = value

    override fun component2(): (U) -> Unit = { value = it }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LensMutableState<*, *>

        if (state != other.state) return false
        if (lens != other.lens) return false
        if (derived != other.derived) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + lens.hashCode()
        result = 31 * result + derived.hashCode()
        return result
    }
}
