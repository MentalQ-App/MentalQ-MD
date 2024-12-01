package com.c242_ps246.mentalq.ui.main.note.detail

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.ui.utils.Utils.formatDate

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNoteScreen(
    noteId: String,
    onBackClick: () -> Unit
) {
    val viewModel: NoteDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title: String? by viewModel.title.collectAsStateWithLifecycle()
    val content: String? by viewModel.content.collectAsStateWithLifecycle()
    val date: String? by viewModel.date.collectAsStateWithLifecycle()
    val selectedEmotion: String? by viewModel.emotion.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.saveNoteImmediately()
    }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(
                    onClick = { viewModel.saveNoteImmediately() },
                    enabled = !uiState.isSaving
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.back),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        )

        if (uiState.isSaving) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.updating_note),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${uiState.error}")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = title!!,
                    onValueChange = { viewModel.updateTitle(it) },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                    decorationBox = { innerTextField ->
                        if (title.isNullOrEmpty()) {
                            Text(
                                text = stringResource(id = R.string.title_placeholder),
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                Text(
                    text = formatDate(date!!),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                BasicTextField(
                    value = content!!,
                    onValueChange = { viewModel.updateContent(it) },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                    decorationBox = { innerTextField ->
                        if (content.isNullOrEmpty()) {
                            Text(
                                text = stringResource(id = R.string.content_placeholder),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                Text(
                    text = stringResource(id = R.string.how_do_you_feel),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                val emotionList = listOf(
                    stringResource(id = R.string.happy),
                    stringResource(id = R.string.anxious),
                    stringResource(id = R.string.angry),
                    stringResource(id = R.string.sad)
                )
                val emoji = listOf("😆", "😰", "😡", "😕")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(emotionList.size) { index ->
                        Spacer(modifier = Modifier.width(8.dp))
                        EmotionButton(
                            emotion = emotionList[index],
                            emoji = emoji[index],
                            selectedEmotion = selectedEmotion
                        ) {
                            viewModel.updateEmotion(emotionList[index])
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun EmotionButton(
    emotion: String,
    emoji: String,
    selectedEmotion: String?,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (selectedEmotion == emotion) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .wrapContentSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        onClick = { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(82.dp)
                .height(82.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Text(
                text = emotion,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewDetailNoteScreen() {
    DetailNoteScreen(
        noteId = "1",
        onBackClick = {}
    )
}

