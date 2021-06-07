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
) : StateObject by (derived as StateObject), State<U> by derived
