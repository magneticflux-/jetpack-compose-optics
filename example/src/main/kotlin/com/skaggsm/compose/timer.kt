package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import arrow.optics.Getter
import arrow.optics.Iso
import arrow.optics.optics
import com.skaggsm.compose.lenses.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToLong
import kotlin.reflect.KProperty1

@optics
data class TimerState(
    val start: Instant = Instant.now(),
    val now: Instant = Instant.now(),
    val maxDuration: Duration = Duration.ofSeconds(5)
) {
    val elapsed: Duration = Duration.between(start, now).coerceAtMost(maxDuration)
    val elapsedFraction: Float = if (maxDuration.isZero) 1f else elapsed.toMillis().toFloat() / maxDuration.toMillis()

    companion object
}

val durationToFloat: Iso<Duration, Float> = Iso(
    {
        it.toNanos() / 1e9f
    },
    {
        Duration.ofNanos((it * 1e9).roundToLong())
    }
)

val roundToTenths = Getter<Duration, String> {
    String.format("%.1f", it.toMillis() / 1000.0)
}

fun main() = Window {
    val state = remember { mutableStateOf(TimerState()) }

    val progress by state.get(TimerState::elapsedFraction)
    val elapsed by state.get(TimerState::elapsed + roundToTenths)

    var start by state.get(TimerState.start)
    var now by state.get(TimerState.now)
    var durationFloat by state.get(TimerState.maxDuration + durationToFloat)

    // Timer update (every 10ms)
    val scope = rememberCoroutineScope()
    scope.launch {
        flow {
            while (true) {
                delay(10)
                emit(Instant.now())
            }
        }.collect {
            now = it
        }
    }

    MaterialTheme {
        Column {
            Row {
                Text("Elapsed Time:")
                Column {
                    LinearProgressIndicator(progress)
                    Text(elapsed)
                }
            }
            Slider(durationFloat, { durationFloat = it }, valueRange = 0f..30f)
            Button({
                start = Instant.now()
            }) {
                Text("Reset Timer")
            }
        }
    }
}

/**
 * Hopefully eventually upstreamed to Arrow
 */
operator fun <T, V, U> KProperty1<T, V>.plus(other: Getter<V, U>): Getter<T, U> {
    return Getter {
        other.get(this(it))
    }
}
