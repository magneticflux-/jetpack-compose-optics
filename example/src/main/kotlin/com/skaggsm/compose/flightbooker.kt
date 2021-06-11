@file:Suppress("FunctionName")

package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import arrow.optics.optics

@optics
data class FlightDateState(var leaveData: String, var returnData: String) {
    companion object
}

@Composable
fun TripTypeMenu() {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("one-way flight", "return flight")
    var selectedIndex by remember { mutableStateOf(0) }
    Column {
        Text(items[selectedIndex],modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true }).background(
            Color.White))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                }) {
                    Text(text = s)
                }
            }
        }
    }
}

@Composable
fun LeaveDateText(label: String, textState: MutableState<FlightDateState>) {
    var text by textState
    TextField(
        text.leaveData,
        onValueChange = { text.leaveData = it },
        label = { Text(label) },
        singleLine = true
    )
}

fun main() = Window {
    val state = remember { mutableStateOf(FlightDateState("", "")) }

    MaterialTheme {
        Column {
            TripTypeMenu()
            LeaveDateText("Leave date...", state)
        }
    }
}