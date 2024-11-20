package com.c242_ps246.mentalq.ui.main.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

@Composable
fun NoteScreen(
    uiState: NoteScreenUiState,
    listNote: List<ListNoteItem>,
//    viewModel: NoteViewModel = hiltViewModel(),
    onNavigateToNoteDetail: (String) -> Unit
) {
//    val uiState by viewModel.uiState.collectAsState()
//    val listNote by viewModel.listNote.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadAllNotes()
//    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.your_note),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                        )
                        Button(
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            onClick = {
                                // TODO: Add Notes Logic Here
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.PostAdd,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = "Add",
                            )
                            Spacer(Modifier.padding(horizontal = 3.dp))
                            Text(
                                text = "ADD",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (!uiState.error.isNullOrEmpty()) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(listNote) { index, item ->
                                NoteItem(
                                    data = ListNoteItem(
                                        id = item.id.toString(),
                                        title = item.title,
                                        content = item.content,
                                        updatedAt = item.updatedAt,
                                        createdAt = item.createdAt
                                    ),
                                    onItemClick = { note ->
                                        onNavigateToNoteDetail(note.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    data: ListNoteItem,
    onItemClick: (ListNoteItem) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = { onItemClick(data) },
                    onLongClick = {
                        showMenu = true
                    }
                )
                .onGloballyPositioned { coordinates ->
                    val cardPos = coordinates.positionInParent()
                    val cardHeight = coordinates.size.height.toFloat()
                    menuOffset = Offset(
                        cardPos.x + coordinates.size.width.toFloat(),
                        (-(cardHeight / 3))
                    )
                    println(cardHeight)
                },

            ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = data.title ?: "",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.content ?: "",
                    style = TextStyle(
                        fontSize = 14.sp
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.createdAt ?: "",
                    style = TextStyle(
                        fontSize = 12.sp
                    )
                )
            }
            if (showMenu) {
                DropdownMenu(
                    modifier = Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    ),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = DpOffset(menuOffset.x.dp, menuOffset.y.dp),
                    shape = RoundedCornerShape(10.dp),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 4.dp
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            /* TODO: Insert DELETE Logic Here
                            *   */
                        },
                        text = {
                            Row {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = "Add",
                                )
                                Spacer(Modifier.padding(horizontal = 4.dp))
                                Text(color = MaterialTheme.colorScheme.error, text = "Delete")
                            }
                        })
                }
            }
        }
    }


}

@Preview(
    name = "Dark Mode Preview",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewNoteScreen() {
    val mockNotes = listOf(
        ListNoteItem(
            "1",
            "Aku sedih banget",
            "Lorem ipsum dolor sit amet",
            "Angry",
            createdAt = "24-12-2024"
        ),
        ListNoteItem(
            "2",
            "Hari ini senang sekali",
            "Lorem ipsum dolor sit amet",
            "Happy",
            createdAt = "24-12-2024"
        ),
        ListNoteItem(
            "3",
            "Gaada yang terjadi hari ini",
            "Lorem ipsum dolor sit amet",
            "Happy",
            createdAt = "24-12-2024"
        ),
    )
    MentalQTheme {
        NoteScreen(
            uiState = NoteScreenUiState(isLoading = false),
            listNote = mockNotes
        ) {}
    }
}