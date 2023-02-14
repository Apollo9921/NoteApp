package com.example.noteapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.noteapp.R
import com.example.noteapp.model.data.Note
import com.example.noteapp.model.data.NoteDatabase
import com.example.noteapp.view.customFunctions.*
import com.example.noteapp.view.navigation.Destination
import com.example.noteapp.view.theme.*
import com.example.noteapp.viewModel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var ID = 0
private var titleUpdate = mutableStateOf("")
private var descriptionUpdate = mutableStateOf("")
private var updateNote = mutableStateOf(false)
private var empty = mutableStateOf(false)
private var sameData = mutableStateOf(false)
private var success = mutableStateOf(false)
private var tasksUpdate = ""
private lateinit var noteViewModel: NoteViewModel
private lateinit var note: Note
private var width: Dp = 0.dp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UpdateNote(navHostController: NavHostController, id: Int?) {
    width = mediaQueryWidth()
    val checkedState: ArrayList<Boolean> = ArrayList()
    val owner = LocalLifecycleOwner.current
    ID = id!!
    NoteDatabase.getDatabase(LocalContext.current).noteDao().listSelectedNote(id).observe(owner) {
        note = it!!
        titleUpdate.value = note.title
        descriptionUpdate.value = note.description
        tasksUpdate = note.tasks
        loaded.value = true
    }
    Scaffold(topBar = { TopBar(navHostController) }) {
        Form(checkedState)
        GetResult(checkedState)
    }
}

@Composable
private fun GetResult(checkedState: ArrayList<Boolean>) {
    when {
        updateNote.value -> {
            updateNote.value = false
            updateData(LocalContext.current, checkedState)
        }
        empty.value -> {
            CustomSnackBar(
                width = width,
                backgroundColor = Black,
                text = stringResource(id = R.string.emptyFields)
            )
        }
        sameData.value -> {
            CustomSnackBar(
                width = width,
                backgroundColor = Orange,
                text = stringResource(id = R.string.fieldsEqual)
            )
        }
        success.value -> {
            CustomSnackBar(
                width = width,
                backgroundColor = Green,
                text = stringResource(id = R.string.noteUpdated)
            )
        }
    }
}

@Composable
private fun TopBar(navHostController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple40),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(painter = painterResource(id = R.drawable.back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier
                .size(
                    if (width <= small) {
                        50.dp
                    } else if (width <= normal) {
                        55.dp
                    } else {
                        60.dp
                    }
                )
                .clickable {
                    navHostController.popBackStack(Destination.Home.route, true)
                    navHostController.navigate(Destination.Home.route)
                })
        Text(
            text = stringResource(id = R.string.updateNote),
            color = White,
            fontSize =
            if (width <= small) {
                20.sp
            } else if (width <= normal) {
                25.sp
            } else {
                30.sp
            },
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun Form(checkedState: ArrayList<Boolean>) {
    val screenHeight = mediaQueryHeight()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        item {
            Column {
                TextField(
                    value = if (loaded.value) titleUpdate.value else "",
                    onValueChange = {
                        titleUpdate.value = it
                    },
                    placeholder = {
                        Text(
                            text = "Update Title",
                            color = Color.Gray,
                            fontSize =
                            if (width <= small) {
                                16.sp
                            } else if (width <= normal) {
                                20.sp
                            } else {
                                24.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        backgroundColor = White,
                        leadingIconColor = Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Black
                    ),
                    shape = CutCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )

                TextField(value = if (loaded.value) descriptionUpdate.value else "",
                    onValueChange = {
                        descriptionUpdate.value = it
                    }, placeholder = {
                        Text(
                            text = "Update the note",
                            color = Color.Gray,
                            fontSize =
                            if (width <= small) {
                                16.sp
                            } else if (width <= normal) {
                                20.sp
                            } else {
                                24.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold
                        )
                    }, keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ), colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        backgroundColor = White,
                        leadingIconColor = Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Black
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight / 2)
                        .padding(20.dp)
                )
            }
        }
        item {
            if (tasksUpdate != "null" && loaded.value) {
                val splitTasksToDo = note.tasksToDo.split(",").toMutableList()
                val splitTasks = note.tasks.split(",").toMutableList()
                for (i in splitTasks.indices) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            if (checkedState.size >= splitTasksToDo.size) {
                                if (!checkedState[i]) {
                                    checkedState.add(i, false)
                                } else {
                                    checkedState.add(i, true)
                                }
                                checkedState.removeAt((i + 1) - 1)
                            } else {
                                if (splitTasksToDo[i].contains("false")) {
                                    checkedState.add(i, false)
                                } else {
                                    checkedState.add(i, true)
                                }
                            }
                            val state = remember { mutableStateOf(checkedState[i]) }
                            Checkbox(
                                checked = state.value,
                                onCheckedChange = {
                                    state.value = it
                                    checkedState[i] = it
                                    splitTasksToDo[i] = it.toString()
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Purple40,
                                    uncheckedColor = White
                                ),
                                modifier = Modifier
                                    .size(
                                        if (width <= small) {
                                            25.dp
                                        } else if (width <= normal) {
                                            30.dp
                                        } else {
                                            35.dp
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.padding(start = 5.dp))
                            Text(
                                text =
                                if (splitTasks.size == 1) {
                                    " " + splitTasks[i].substring(1, splitTasks[i].length - 1)
                                }else if (splitTasks[i].contains("[")) {
                                    " " + splitTasks[i].substring(1)
                                } else if (splitTasks[i].contains("]")) {
                                    splitTasks[i].substring(0, splitTasks[i].length - 1)
                                } else {
                                    splitTasks[i]
                                },
                                color = White,
                                fontSize =
                                if (width <= small) {
                                    16.sp
                                } else if (width <= normal) {
                                    20.sp
                                } else {
                                    24.sp
                                },
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        Spacer(modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
        item {
            Button(
                onClick = {
                    updateNote.value = true
                },
                border = BorderStroke(1.dp, White),
                shape = RoundedCornerShape(30),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple40)
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.updateNote),
                        color = White,
                        fontSize =
                        if (width <= small) {
                            20.sp
                        } else if (width <= normal) {
                            25.sp
                        } else {
                            30.sp
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun updateData(context: Context, checkedState: ArrayList<Boolean>) {
    noteViewModel = NoteViewModel(context)
    if (titleUpdate.value.isNotBlank() && descriptionUpdate.value.isNotBlank()) {
        if (titleUpdate.value != note.title ||
            descriptionUpdate.value != note.description ||
            checkedState.toString() != note.tasksToDo
        ) {
            val currentDateTime: String =
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            val note = Note(
                ID,
                titleUpdate.value,
                descriptionUpdate.value,
                tasksUpdate,
                checkedState.toString(),
                currentDateTime
            )
            noteViewModel.updateNote(note)
            success.value = true
            Handler(Looper.getMainLooper()).postDelayed({
                success.value = false
            }, 1500)
        } else {
            sameData.value = true
            Handler(Looper.getMainLooper()).postDelayed({
                sameData.value = false
            }, 2000)
        }
    } else {
        empty.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            empty.value = false
        }, 2000)
    }
}