package com.example.noteapp.view

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.ui.text.style.TextOverflow
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

private lateinit var notesList: SnapshotStateList<Note>
private lateinit var notesListSearched: SnapshotStateList<Note>
private lateinit var deletedNote: SnapshotStateList<Note>
private var item: Note? = null
private var id = 0
private var noteSelected = mutableStateOf(false)
private var showSearchTextField = mutableStateOf(false)
private var search = mutableStateOf("")
private lateinit var noteViewModel: NoteViewModel
private lateinit var listState: LazyListState
private var width: Dp = 0.dp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Home(navHostController: NavHostController, onDataLoaded: () -> Unit) {
    width = mediaQueryWidth()
    listState = rememberLazyListState()
    val owner = LocalLifecycleOwner.current
    noteViewModel = NoteViewModel(LocalContext.current)
    notesList = remember { mutableStateListOf() }
    notesListSearched = remember { mutableStateListOf() }
    deletedNote = remember { mutableStateListOf() }
    noteViewModel.listAllNotes.observe(owner) { note ->
        if (notesList.isNotEmpty()) {
            notesList.clear()
        }
        for (i in note.indices) {
            notesList.add(note[i])
        }
        loaded.value = true
        onDataLoaded()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar()
        },
        floatingActionButton = {
            if (!showSearchTextField.value) {
                FloatActionButton(navHostController)
            }
        }
    ) {
        if (notesList.isNotEmpty()) {
            ListNotes(navHostController)
        } else {
            EmptyNotes()
        }
    }
}

@Composable
private fun TopBar() {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    if (search.value.isNotBlank()) {
        NoteDatabase.getDatabase(context)
            .noteDao()
            .searchNote("%${search.value}%")
            .observe(owner) { search ->
                if (notesListSearched.isNotEmpty()) {
                    notesListSearched.clear()
                }
                if (search.isNotEmpty()) {
                    for (i in search.indices) {
                        notesListSearched.add(search[i])
                    }
                }
            }
    }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple40)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (loaded.value) {
                Text(
                    text = stringResource(id = R.string.totalNotes, notesList.size),
                    color = White,
                    fontSize =
                    if (width <= small) {
                        20.sp
                    } else if (width <= normal) {
                        25.sp
                    } else {
                        30.sp
                    }
                )
            }
            if (noteSelected.value) {
                Image(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier
                        .size(
                            if (width <= small) {
                                45.dp
                            } else if (width <= normal) {
                                50.dp
                            } else {
                                55.dp
                            }
                        )
                        .clickable {
                            noteSelected.value = false
                            deletedNote.add(item!!)
                        }
                )
            } else if (!noteSelected.value && !showSearchTextField.value) {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier
                        .size(
                            if (width <= small) {
                                45.dp
                            } else if (width <= normal) {
                                50.dp
                            } else {
                                55.dp
                            }
                        )
                        .clickable {
                            if (notesList.isNotEmpty()) {
                                showSearchTextField.value = true
                            }
                        }
                )
            }
        }
    }
    if (loaded.value && notesList.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple40),
            horizontalArrangement = Arrangement.End
        ) {
            if (showSearchTextField.value) {
                TextField(
                    value = search.value,
                    onValueChange = {
                        search.value = it
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.searchNotes),
                            color = White,
                            fontSize =
                            if (width <= small) {
                                15.sp
                            } else if (width <= normal) {
                                20.sp
                            } else {
                                25.sp
                            }
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (notesListSearched.isNotEmpty()) {
                                    notesListSearched.clear()
                                }
                                search.value = ""
                                showSearchTextField.value = false
                            }
                        ) {
                            Image(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(White),
                                modifier = Modifier
                                    .size(
                                        if (width <= small) {
                                            45.dp
                                        } else if (width <= normal) {
                                            50.dp
                                        } else {
                                            55.dp
                                        }
                                    )
                            )
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = White,
                        focusedIndicatorColor = White,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = White,
                        focusedLabelColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun FloatActionButton(navHostController: NavHostController) {
    var enabled by remember { mutableStateOf(true) }
    FloatingActionButton(
        onClick = {
            if (enabled) {
                enabled = false
                if (notesListSearched.isNotEmpty()) {
                    notesListSearched.clear()
                }
                loaded.value = false
                navHostController.navigate(Destination.AddNote.route)
            }
        },
        shape = CircleShape,
        backgroundColor = Purple40,
        contentColor = White
    ) {
        Image(
            painter = painterResource(id = R.drawable.add),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White)
        )
    }
}

@Composable
private fun EmptyNotes() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier
                .size(
                    if (width <= small) {
                        150.dp
                    } else if (width <= normal) {
                        200.dp
                    } else {
                        250.dp
                    }
                )
        )
        Text(
            text = stringResource(id = R.string.emptyNotes),
            color = White,
            fontSize =
            if (width <= small) {
                20.sp
            } else if (width <= normal) {
                25.sp
            } else {
                30.sp
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListNotes(navHostController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState
    ) {
        if (notesListSearched.isEmpty() && !showSearchTextField.value) {
            items(notesList.size) { i ->
                AnimatedVisibility(
                    visible = !deletedNote.contains(notesList[i]),
                    exit = shrinkVertically(animationSpec = tween(1000)),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Card(
                        backgroundColor = White,
                        border = BorderStroke(3.dp, Black),
                        elevation = 10.dp,
                        shape = CutCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp)
                            .combinedClickable(
                                onClick = {
                                    if (noteSelected.value) {
                                        id = 0
                                        item = null
                                        noteSelected.value = false
                                    } else {
                                        loaded.value = false
                                        navHostController.navigate(
                                            Destination.UpdateNote.passArguments(notesList[i].id)
                                        )
                                    }
                                },
                                onLongClick = {
                                    id = i
                                    item = notesList[i]
                                    noteSelected.value = true
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            Row {
                                Text(
                                    text = notesList[i].title,
                                    color = Black,
                                    fontSize =
                                    if (width <= small) {
                                        25.sp
                                    } else if (width <= normal) {
                                        30.sp
                                    } else {
                                        35.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                if (noteSelected.value && item == notesList[i]) {
                                    Image(
                                        painter = painterResource(id = R.drawable.check),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Green),
                                        modifier = Modifier.size(
                                            if (width <= small) {
                                                40.dp
                                            } else if (width <= normal) {
                                                45.dp
                                            } else {
                                                50.dp
                                            }
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = notesList[i].description,
                                color = Black,
                                fontSize =
                                if (width <= small) {
                                    15.sp
                                } else if (width <= normal) {
                                    20.sp
                                } else {
                                    25.sp
                                },
                                fontFamily = FontFamily.SansSerif,
                                maxLines = 6,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (notesList[i].tasks != "null") {
                                val split = notesList[i].tasks.split(",")
                                val tasksSize = notesList[i].tasksToDo.split(",")
                                val completed: ArrayList<Boolean> = ArrayList()
                                for (y in tasksSize.indices) {
                                    if (tasksSize[y].contains("true")) {
                                        completed.add(true)
                                    }
                                }
                                Spacer(modifier = Modifier.padding(10.dp))
                                Row {
                                    Image(
                                        painter = painterResource(id = R.drawable.check),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Green),
                                        modifier = Modifier.size(
                                            if (width <= small) {
                                                20.dp
                                            } else if (width <= normal) {
                                                25.dp
                                            } else {
                                                30.dp
                                            }
                                        )
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.totalTasksAndCompleted,
                                            completed.size,
                                            split.size
                                        ),
                                        color = Black,
                                        fontSize =
                                        if (width <= small) {
                                            15.sp
                                        } else if (width <= normal) {
                                            20.sp
                                        } else {
                                            25.sp
                                        },
                                        fontFamily = FontFamily.SansSerif,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.End
                            ) {
                                val dateTime =
                                    notesList[i].date.substring(0, notesList[i].date.length - 3)
                                Text(
                                    text = dateTime,
                                    color = Black,
                                    fontSize =
                                    if (width <= small) {
                                        10.sp
                                    } else if (width <= normal) {
                                        15.sp
                                    } else {
                                        20.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else if (notesListSearched.isNotEmpty()) {
            items(notesListSearched.size) { i ->
                AnimatedVisibility(
                    visible = !deletedNote.contains(notesListSearched[i]),
                    exit = shrinkVertically(animationSpec = tween(1000)),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Card(
                        backgroundColor = White,
                        border = BorderStroke(3.dp, Black),
                        elevation = 10.dp,
                        shape = CutCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp)
                            .combinedClickable(
                                onClick = {
                                    if (noteSelected.value) {
                                        id = 0
                                        item = null
                                        noteSelected.value = false
                                        showSearchTextField.value = true
                                    } else {
                                        loaded.value = false
                                        navHostController.navigate(
                                            Destination.UpdateNote.passArguments(
                                                notesListSearched[i].id
                                            )
                                        )
                                    }
                                },
                                onLongClick = {
                                    id = i
                                    item = notesListSearched[i]
                                    noteSelected.value = true
                                    showSearchTextField.value = false
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            Row {
                                Text(
                                    text = notesListSearched[i].title,
                                    color = Black,
                                    fontSize =
                                    if (width <= small) {
                                        25.sp
                                    } else if (width <= normal) {
                                        30.sp
                                    } else {
                                        35.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                if (noteSelected.value && item == notesListSearched[i]) {
                                    Image(
                                        painter = painterResource(id = R.drawable.check),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Green),
                                        modifier = Modifier.size(
                                            if (width <= small) {
                                                40.dp
                                            } else if (width <= normal) {
                                                45.dp
                                            } else {
                                                50.dp
                                            }
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = notesListSearched[i].description,
                                color = Black,
                                fontSize =
                                if (width <= small) {
                                    15.sp
                                } else if (width <= normal) {
                                    20.sp
                                } else {
                                    25.sp
                                },
                                fontFamily = FontFamily.SansSerif,
                                maxLines = 6,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (notesListSearched[i].tasks != "null") {
                                val split = notesListSearched[i].tasks.split(",")
                                val tasksSize = notesListSearched[i].tasksToDo.split(",")
                                val completed: ArrayList<Boolean> = ArrayList()
                                for (y in tasksSize.indices) {
                                    if (tasksSize[y].contains("true")) {
                                        completed.add(true)
                                    }
                                }
                                Spacer(modifier = Modifier.padding(10.dp))
                                Row {
                                    Image(
                                        painter = painterResource(id = R.drawable.check),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(Green),
                                        modifier = Modifier.size(
                                            if (width <= small) {
                                                20.dp
                                            } else if (width <= normal) {
                                                25.dp
                                            } else {
                                                30.dp
                                            }
                                        )
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.totalTasksAndCompleted,
                                            completed.size,
                                            split.size
                                        ),
                                        color = Black,
                                        fontSize =
                                        if (width <= small) {
                                            15.sp
                                        } else if (width <= normal) {
                                            20.sp
                                        } else {
                                            25.sp
                                        },
                                        fontFamily = FontFamily.SansSerif,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.End
                            ) {
                                val dateTime =
                                    notesListSearched[i].date.substring(
                                        0,
                                        notesListSearched[i].date.length - 3
                                    )
                                Text(
                                    text = dateTime,
                                    color = Black,
                                    fontSize =
                                    if (width <= small) {
                                        10.sp
                                    } else if (width <= normal) {
                                        15.sp
                                    } else {
                                        20.sp
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(mediaQueryHeight() / 2),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(White),
                        modifier = Modifier.size(
                            if (width <= small) {
                                150.dp
                            } else if (width <= normal) {
                                200.dp
                            } else {
                                250.dp
                            }
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.noResults),
                        color = White,
                        fontSize =
                        if (width <= small) {
                            20.sp
                        } else if (width <= normal) {
                            25.sp
                        } else {
                            30.sp
                        }
                    )
                }
            }
        }
    }
    if (deletedNote.contains(item)) {
        val timer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                notesList.remove(item)
                noteViewModel.deleteNote(item!!)
                item = null
            }
        }
        timer.start()
    }
}