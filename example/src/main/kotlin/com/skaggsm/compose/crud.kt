@file:Suppress("FunctionName")

package com.skaggsm.compose

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import arrow.optics.optics
import com.skaggsm.compose.lenses.get

@optics
data class Person(val name: String, val surname: String) {
    override fun toString(): String {
        return "$surname, $name"
    }

    companion object
}

@optics
data class CrudState(
    val filter: String = "",
    val selectedIndex: Int = 0,
    val people: List<Person> = listOf(
        Person("Hans", "Emil"),
        Person("Max", "Mustermann"),
        Person("Roman", "Tisch")
    ),
    val personDisplayed: Person = Person("", "")
) {
    val filteredPeople: List<Person>
        get() = people.filter { it.toString().contains(filter, ignoreCase = true) }

    companion object
}

@Composable
fun EditText(prefix: String, filter: MutableState<String>) =
    TextField(filter.value, { filter.value = it }, label = { Text(prefix) }, modifier = Modifier.padding(4.dp))

@Composable
fun SelectableList(items: State<List<Person>>, selectedIndex: MutableState<Int>) =
    LazyColumn(modifier = Modifier.selectableGroup()) {
        itemsIndexed(items.value) { index: Int, item: Person ->
            val selected = index == selectedIndex.value
            Card(
                backgroundColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                modifier = Modifier
                    .padding(4.dp)
                    .selectable(selected, onClick = {
                        selectedIndex.value = index
                    }),
                elevation = 4.dp
            ) {
                Text("$item", modifier = Modifier.padding(4.dp))
            }
        }
    }

fun main() = Window {
    val state = remember { mutableStateOf(CrudState()) }

    val filterState = state.get(CrudState.filter)
    val peopleState = state.get(CrudState.people)
    val filteredPeopleState = state.get(CrudState::filteredPeople)
    val selectedIndexState = state.get(CrudState.selectedIndex)

    val personDisplayedState = state.get(CrudState.personDisplayed)
    val personDisplayedNameState = personDisplayedState.get(Person.name)
    val personDisplayedSurnameState = personDisplayedState.get(Person.surname)

    MaterialTheme {
        Column {
            EditText("Filter prefix:", filterState)
            Row {
                SelectableList(filteredPeopleState, selectedIndexState)
                Column {
                    EditText("Name:", personDisplayedNameState)
                    EditText("Surname:", personDisplayedSurnameState)
                }
            }
            Row {
                Button({

                }) {
                    Text("Create")
                }
                Button({
                }) {
                    Text("Update")
                }
                Button({
                }) {
                    Text("Delete")
                }
            }
        }
    }
}
