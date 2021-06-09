package com.skaggsm.compose.lenses

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshots.StateObject
import arrow.optics.Getter

fun <T, U> State<T>.get(getter: Getter<T, U>): State<U> = GetterState(this, getter)

// Might be able to replace with just `derivedStateOf` alone.
internal class GetterState<T, U>(
    private val state: State<T>,
    private val getter: Getter<T, U>,
    private val derived: State<U> = derivedStateOf { getter.get(state.value) }
) : StateObject by (derived as StateObject), State<U> by derived {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetterState<*, *>

        if (state != other.state) return false
        if (getter != other.getter) return false
        if (derived != other.derived) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + getter.hashCode()
        result = 31 * result + derived.hashCode()
        return result
    }
}
