package com.skaggsm.compose.lenses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshots.StateObject
import arrow.optics.Iso


fun <T, U> MutableState<T>.get(iso: Iso<T, U>): MutableState<U> = IsoMutableState(this, iso)

internal class IsoMutableState<T, U>(
    private val state: MutableState<T>,
    private val iso: Iso<T, U>,
    private val derived: State<U> = derivedStateOf { iso.get(state.value) }
) : MutableState<U>, StateObject by (derived as StateObject), State<U> by derived {
    override var value: U
        get() = derived.value
        set(value) {
            state.value = iso.set(value)
        }

    override fun component1(): U = value

    override fun component2(): (U) -> Unit = { value = it }
}
