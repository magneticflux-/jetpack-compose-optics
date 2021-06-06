package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import arrow.optics.optics
import com.skaggsm.compose.lenses.get

@optics
data class CounterState(val counter1: Int = 0, val counter2: Int = 1) {
    companion object
}

@Composable
fun Counter(state: MutableState<Int>) {
    var counter by state
    return Row(Modifier.padding(2.dp)) {
        println("Recomposing counter")
        Button(onClick = {
            counter += 1
        }) {
            Text("Increment")
        }
        Text("Count: $counter")
    }
}

fun main() = Window {
    val state = remember { mutableStateOf(CounterState()) }

    MaterialTheme {
        Column {
            println("Recomposing main")
            Counter(state.get(CounterState.counter1))
            Counter(state.get(CounterState.counter2))
        }
    }
}
