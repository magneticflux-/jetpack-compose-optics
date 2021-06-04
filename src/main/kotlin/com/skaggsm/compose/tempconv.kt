package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import arrow.core.toOption
import arrow.optics.Iso
import arrow.optics.Optional
import arrow.optics.Prism
import arrow.optics.optics
import com.skaggsm.compose.Temperature.Celsius
import com.skaggsm.compose.Temperature.Fahrenheit
import com.skaggsm.compose.lenses.get
import kotlin.math.roundToInt

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

val cToF = Option.lift<Celsius, Fahrenheit> { Fahrenheit(it.temp * 9 / 5 + 32) }
val fToC = Option.lift<Fahrenheit, Celsius> { Celsius((it.temp - 32) * 5 / 9) }

val cToFIso = Iso(cToF, fToC)

val parseString = Optional<String, Double>(
    { it.toDoubleOrNull().toOption() },
    { _, d -> d.toString() }
)

val stringParser: Prism<String, Double> = Prism(
    {
        it.toDoubleOrNull().toOption()
    }, {
        it.toString()
    }
)

@Composable
fun MyTextField(text: MutableState<String>) {
    return TextField(text.value, { text.value = it })
}

fun main() = Window {
    val state = remember { mutableStateOf(TempConvState(None)) }

    val cTextState = remember { mutableStateOf("") }
    var cText by cTextState
    var fText by remember { mutableStateOf("") }

    var tempC by state.get(TempConvState.optionTempC)
    var tempF by state.get(TempConvState.optionTempC + cToFIso)

    MaterialTheme {
        Row {
            MyTextField(cTextState)
            Row {
                TextField(
                    cText,
                    onValueChange = { s ->
                        cText = s
                        stringParser.getOrNull(s)?.let {
                            tempC = Celsius(it).some()
                        }
                        tempF.orNull()?.temp?.roundToInt()?.let {
                            fText = "$it"
                        }
                    },
                    label = { Text("Celsius") },
                    isError = stringParser.getOrNull(cText) == null
                )
            }
            Text("=")
            Row {
                TextField(
                    fText,
                    onValueChange = { s ->
                        fText = s
                        stringParser.getOrNull(s)?.let {
                            tempF = Fahrenheit(it).some()
                        }
                        tempC.orNull()?.temp?.roundToInt()?.let {
                            cText = "$it"
                        }
                    },
                    label = { Text("Fahrenheit") },
                    isError = stringParser.getOrNull(fText) == null
                )
            }
        }
    }
}
