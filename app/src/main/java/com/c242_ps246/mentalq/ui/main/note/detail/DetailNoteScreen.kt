package com.c242_ps246.mentalq.ui.main.note.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.c242_ps246.mentalq.ui.utils.Utils.getTodayDate
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
    val todayDate = getTodayDate()
    val defaultTitle = stringResource(id = R.string.note_title)
    val defaultContent = stringResource(id = R.string.note_content)

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    viewModel.updateRemoteNote()
                    onBackClick()
                })
                {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            title = {
                Text(
                    text = "Back",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = title ?: defaultTitle,
                    onValueChange = { viewModel.updateTitle(it) },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = formatDate(date ?: todayDate),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                BasicTextField(
                    value = content ?: defaultContent,
                    onValueChange = { viewModel.updateContent(it) },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
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

