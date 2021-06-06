package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import arrow.core.*
import arrow.optics.*
import com.skaggsm.compose.Temperature.Celsius
import com.skaggsm.compose.Temperature.Fahrenheit
import com.skaggsm.compose.lenses.get
import com.skaggsm.compose.lenses.reverseGet

@optics
data class TempConvState(val tempC: Option<Celsius>) {
    companion object
}

@optics
sealed interface Temperature {
    val temp: Double

    @JvmInline
    value class Celsius(override val temp: Double) : Temperature

    @JvmInline
    value class Fahrenheit(override val temp: Double) : Temperature

    companion object
}

val celsiusIso = Iso(Option.lift(::Celsius), Option.lift(Celsius::temp))
val fahrenheitIso = Iso(Option.lift(::Fahrenheit), Option.lift(Fahrenheit::temp))

val cToF = Option.lift<Celsius, Fahrenheit> { Fahrenheit(it.temp * 9 / 5 + 32) }
val fToC = Option.lift<Fahrenheit, Celsius> { Celsius((it.temp - 32) * 5 / 9) }

val cToFIso = Iso(cToF, fToC)

val stringParser: Prism<String, Option<Double>> = Prism(
    {
        when (val result = it.toDoubleOrNull().toOption()) {
            is Some -> result.some()
            is None -> None
        }
    }, {
        it.map(Double::toString).getOrElse { "" }
    }
)
val celsiusParser = stringParser + celsiusIso
val fahrenheitParser = stringParser + fahrenheitIso

fun main() = Window {
    Snapshot.registerGlobalWriteObserver {
        println("Wrote $it")
    }
    val state = remember { mutableStateOf(TempConvState(None)) }

    val tempCState = state.get(TempConvState.optionTempC)
    val tempCText = tempCState.reverseGet(celsiusParser)
    var cText by tempCText

    val tempFState = tempCState.get(cToFIso)
    val tempFText = tempFState.reverseGet(fahrenheitParser)
    var fText by tempFText

    MaterialTheme {
        Row {
            Row {
                TextField(
                    cText,
                    onValueChange = { cText = it },
                    label = { Text("Celsius") },
                    isError = stringParser.getOrNull(cText) == null
                )
            }
            Text("=")
            Row {
                TextField(
                    fText,
                    onValueChange = { fText = it },
                    label = { Text("Fahrenheit") },
                    isError = stringParser.getOrNull(fText) == null
                )
            }
        }
    }
}
