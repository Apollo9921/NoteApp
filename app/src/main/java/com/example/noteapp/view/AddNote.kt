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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
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
import com.example.noteapp.view.customFunctions.*
import com.example.noteapp.view.navigation.Destination
import com.example.noteapp.view.theme.*
import com.example.noteapp.viewModel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var title = mutableStateOf("")
private var description = mutableStateOf("")
private var task = mutableStateOf("")
private var createNote = mutableStateOf(false)
private var empty = mutableStateOf(false)
private var success = mutableStateOf(false)
private var tasks: MutableList<String> = ArrayList()
private lateinit var tasksInserted: SnapshotStateList<String>
private var tasksToDo: MutableList<String> = ArrayList()
private lateinit var tasksToDoInserted: SnapshotStateList<String>
private lateinit var noteViewModel: NoteViewModel
private var width: Dp = 0.dp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddNote(navHostController: NavHostController) {
    width = mediaQueryWidth()
    Scaffold(
        topBar = { TopBar(navHostController) }
    ) {
        Form()
        GetResult()
    }
    loaded.value = false
}

@Composable
private fun GetResult() {
    when {
        createNote.value -> {
            createNote.value = false
            insertData(LocalContext.current)
        }
        empty.value -> {
            CustomSnackBar(
                width = width,
                backgroundColor = Black,
                text = stringResource(id = R.string.emptyFields)
            )
        }
        success.value -> {
            CustomSnackBar(
                width = width,
                backgroundColor = Green,
                text = stringResource(id = R.string.noteAdded)
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
        Image(
            painter = painterResource(id = R.drawable.back),
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
                }
        )
        Text(
            text = stringResource(id = R.string.addNote),
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
private fun Form() {
    val screenHeight = mediaQueryHeight()
    tasksInserted = remember { mutableStateListOf() }
    tasksToDoInserted = remember { mutableStateListOf() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        item {
            Column {
                TextField(
                    value = title.value,
                    onValueChange = {
                        title.value = it
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.insertTitle),
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

                TextField(
                    value = description.value,
                    onValueChange = {
                        description.value = it
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.descriptionNote),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight / 2)
                        .padding(20.dp)
                )

                Row {
                    TextField(
                        value = task.value,
                        onValueChange = {
                            task.value = it
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.addTask),
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
                            .width(mediaQueryWidth() / 2)
                            .padding(start = 20.dp)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Button(
                        onClick = {
                            if (task.value != "") {
                                tasksInserted.add(task.value)
                                tasksToDoInserted.add("false")
                                task.value = ""
                            }
                        },
                        border = BorderStroke(1.dp, White),
                        shape = RoundedCornerShape(30),
                    ) {
                        Box(
                            modifier = Modifier.background(Purple40),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.createTask),
                                color = White,
                                fontSize =
                                if (width <= small) {
                                    16.sp
                                } else if (width <= normal) {
                                    20.sp
                                } else {
                                    24.sp
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                if (tasksInserted.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(10.dp))
                    for (i in tasksInserted.indices) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp)
                            ) {
                                Text(
                                    text = tasksInserted[i],
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
                                Image(
                                    painter = painterResource(id = R.drawable.close),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Red),
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
                                        .clickable {
                                            tasksInserted.remove(tasksInserted[i])
                                            tasksToDoInserted.remove(tasksToDoInserted[i])
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.padding(10.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Button(
                    onClick = {
                        createNote.value = true
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
                            text = stringResource(id = R.string.createNote),
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
}

private fun insertData(context: Context) {
    noteViewModel = NoteViewModel(context)
    if (title.value.isNotBlank() && description.value.isNotBlank()) {
        val currentDateTime: String =
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val note: Note = if (tasksInserted.isEmpty()) {
            Note(0, title.value, description.value, "null", "null", currentDateTime)
        } else {
            for (i in tasksInserted.indices) {
                tasks.add(tasksInserted[i])
                tasksToDo.add(tasksToDoInserted[i])
            }
            Note(
                0,
                title.value,
                description.value,
                tasks.toString(),
                tasksToDo.toString(),
                currentDateTime
            )
        }
        noteViewModel.addNote(note)
        title.value = ""
        description.value = ""
        tasks.clear()
        tasksInserted.clear()
        success.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            success.value = false
        }, 1500)
    } else {
        empty.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            empty.value = false
        }, 2000)
    }
}