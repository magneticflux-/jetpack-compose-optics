package com.skaggsm.compose.lenses

import androidx.compose.runtime.MutableState
import arrow.optics.Lens


fun <T, U> MutableState<T>.get(lens: Lens<T, U>): MutableState<U> = LensMutableState(this, lens)

internal class LensMutableState<T, U>(
    private val state: MutableState<T>,
    private val lens: Lens<T, U>
) : MutableState<U> {
    override var value: U
        get() = lens.get(state.value)
        set(value) {
            state.value = lens.set(state.value, value)
        }

    override fun component1(): U = value

    override fun component2(): (U) -> Unit = { value = it }
}
