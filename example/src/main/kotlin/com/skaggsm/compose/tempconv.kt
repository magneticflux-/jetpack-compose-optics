@file:Suppress("FunctionName")

package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
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

@Composable
fun TemperatureText(label: String, textState: MutableState<String>) {
    var text by textState
    TextField(
        text,
        onValueChange = { text = it },
        label = { Text(label) },
        isError = stringParser.getOrModify(text).isLeft(),
        singleLine = true
    )
}

fun main() = Window {
    val state = remember { mutableStateOf(TempConvState(None)) }

    val tempCState = state.get(TempConvState.optionTempC)
    val tempCText = tempCState.reverseGet(celsiusParser)

    val tempFState = tempCState.get(cToFIso)
    val tempFText = tempFState.reverseGet(fahrenheitParser)

    MaterialTheme {
        Row {
            TemperatureText("Celsius", tempCText)
            Text("=")
            TemperatureText("Fahrenheit", tempFText)
        }
    }
}
