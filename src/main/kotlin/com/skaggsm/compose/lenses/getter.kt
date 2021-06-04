package com.skaggsm.compose.lenses

import androidx.compose.runtime.State
import arrow.optics.Getter

fun <T, U> State<T>.get(getter: Getter<T, U>): State<U> = GetterState(this, getter)

internal class GetterState<T, U>(
    private val state: State<T>,
    private val getter: Getter<T, U>
) : State<U> {
    override val value: U
        get() = getter.get(state.value)
}
