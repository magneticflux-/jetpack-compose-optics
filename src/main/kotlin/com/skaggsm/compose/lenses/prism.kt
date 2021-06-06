package com.skaggsm.compose.lenses

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.some
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

fun <T, U> MutableState<T>.reverseGet(prism: Prism<U, T>): MutableState<U> = ReversePrismMutableState(this, prism)

internal class ReversePrismMutableState<T, U>(
    private val state: MutableState<T>,
    private val prism: Prism<U, T>,
    private val intermediaryState: MutableState<Option<U>> = mutableStateOf(None)
) : MutableState<U> {
    override var value: U
        get() = prism.reverseGet(state.value)
        set(value) {
            when (val result = prism.getOrModify(value)) {
                is Either.Right -> {
                    state.value = result.value
                }
                is Either.Left -> {
                    intermediaryState.value = result.value.some()
                }
            }
        }

    override fun component1(): U = value

    override fun component2(): (U) -> Unit = { value = it }
}
