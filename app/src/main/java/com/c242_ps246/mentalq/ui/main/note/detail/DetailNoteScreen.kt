package com.c242_ps246.mentalq.ui.main.note.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailNoteScreen(
    viewModel: NoteDetailViewModel,
    noteId: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val selectedEmotion by viewModel.emotion.collectAsStateWithLifecycle()

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text("← Back") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
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
                    value = title,
                    onValueChange = { viewModel.updateTitle(it) },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = uiState.note?.date ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                BasicTextField(
                    value = content,
                    onValueChange = { viewModel.updateContent(it) },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                Text(
                    text = "How are you feeling in this moment?",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EmotionButton("Anxious", "😰", selectedEmotion) {
                        viewModel.updateEmotion("Anxious")
                    }
                    EmotionButton("Happy", "😆", selectedEmotion) {
                        viewModel.updateEmotion("Happy")
                    }
                    EmotionButton("Angry", "😡", selectedEmotion) {
                        viewModel.updateEmotion("Angry")
                    }
                    EmotionButton("Sad", "😕", selectedEmotion) {
                        viewModel.updateEmotion("Sad")
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                if (selectedEmotion == emotion) Color.LightGray else Color(0xFFF5F5F5),
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .clickable { onClick() }
        )
        Text(
            text = emotion,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}
