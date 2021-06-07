package com.skaggsm.compose.lenses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshots.StateObject
import arrow.core.Either
import arrow.optics.Optional

fun <T, U> MutableState<T>.get(optional: Optional<T, U>): MutableState<Either<T, U>> =
    OptionalMutableState(this, optional)

internal class OptionalMutableState<T, U>(
    private val state: MutableState<T>,
    private val optional: Optional<T, U>,
    private val derived: State<Either<T, U>> = derivedStateOf { optional.getOrModify(state.value) }
) : MutableState<Either<T, U>>, StateObject by (derived as StateObject), State<Either<T, U>> by derived {
    override var value: Either<T, U>
        get() = derived.value
        set(value) {
            when (value) {
                is Either.Right -> {
                    state.value = optional.set(state.value, value.value)
                }
                else -> return
            }
        }

    override fun component1(): Either<T, U> = value

    override fun component2(): (Either<T, U>) -> Unit = { value = it }
}
