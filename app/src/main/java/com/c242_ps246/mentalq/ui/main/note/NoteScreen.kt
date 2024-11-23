package com.c242_ps246.mentalq.ui.main.note

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import android.content.res.Configuration
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
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
import com.c242_ps246.mentalq.ui.component.ToastType
import com.c242_ps246.mentalq.ui.theme.MentalQTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.c242_ps246.mentalq.ui.component.CustomToast
import com.c242_ps246.mentalq.ui.utils.Utils.formatDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteScreen(
    viewModel: NoteViewModel = hiltViewModel(),
    onNavigateToNoteDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listNote by viewModel.listNote.collectAsState()

    // Use configuration to determine screen width
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.INFO) }

    val navigateToNoteDetail by viewModel.navigateToNoteDetail.collectAsState()

    // Responsive padding based on screen width
    val screenPadding = when {
        screenWidth < 600.dp -> 16.dp
        screenWidth < 840.dp -> 24.dp
        else -> 32.dp
    }

    LaunchedEffect(uiState) {
        when {
            uiState.error != null -> {
                showToast = true
                toastMessage = uiState.error ?: "An Error Occurred"
                toastType = ToastType.ERROR
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(navigateToNoteDetail) {
        navigateToNoteDetail?.let { noteId ->
            onNavigateToNoteDetail(noteId)
            viewModel.navigateToNoteDetailCompleted()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(screenPadding)
            ) {
                Column {
                    ResponsiveHeader(
                        onAddNote = {
                            viewModel.addNote(
                                ListNoteItem(
                                    id = "",
                                    title = "",
                                    content = "",
                                    emotion = ""
                                )
                            )
                        },
                        isEnabled = !uiState.isCreatingNewNote,
                        screenWidth = screenWidth
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        uiState.isLoading -> {
                            LoadingState()
                        }

                        !uiState.error.isNullOrEmpty() -> {
                            ErrorState(error = uiState.error!!)
                        }

                        uiState.isCreatingNewNote -> {
                            CreatingNoteState()
                        }

                        listNote.isEmpty() -> {
                            EmptyState()
                        }

                        else -> {
                            ResponsiveNoteList(
                                notes = listNote,
                                onItemClick = onNavigateToNoteDetail,
                                onItemDelete = { note -> viewModel.deleteNote(note.id) },
                                screenWidth = screenWidth
                            )
                        }
                    }
                }
            }

            if (showToast) {
                ResponsiveToast(
                    message = toastMessage,
                    type = toastType,
                    onDismiss = { showToast = false }
                )
            }
        }
    }
}

@Composable
private fun ResponsiveHeader(
    onAddNote: () -> Unit,
    isEnabled: Boolean,
    screenWidth: Dp
) {
    val buttonSize = if (screenWidth < 600.dp) 36.dp else 42.dp
    val titleSize = when {
        screenWidth < 600.dp -> 24.sp
        screenWidth < 840.dp -> 28.sp
        else -> 32.sp
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.your_note),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = titleSize
            ),
        )
        Button(
            modifier = Modifier
                .wrapContentWidth()
                .height(buttonSize),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            onClick = onAddNote,
            enabled = isEnabled
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.PostAdd,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "Add",
            )
            Spacer(Modifier.padding(horizontal = 3.dp))
            Text(
                text = stringResource(id = R.string.add_note),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ResponsiveNoteList(
    notes: List<ListNoteItem>,
    onItemClick: (String) -> Unit,
    onItemDelete: (ListNoteItem) -> Unit,
    screenWidth: Dp
) {
    val listWidth = if (screenWidth >= 840.dp) {
        Modifier.fillMaxWidth(0.8f)
    } else {
        Modifier.fillMaxWidth()
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = listWidth
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(notes) { index, item ->
                ResponsiveNoteItem(
                    data = item,
                    onItemClick = onItemClick,
                    onItemDelete = onItemDelete,
                    screenWidth = screenWidth
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ResponsiveNoteItem(
    data: ListNoteItem,
    onItemClick: (String) -> Unit,
    onItemDelete: (ListNoteItem) -> Unit,
    screenWidth: Dp
) {
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    val cardPadding = if (screenWidth < 600.dp) 12.dp else 16.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    onClick = { onItemClick(data.id) },
                    onLongClick = { showMenu = true }
                )
                .onGloballyPositioned { coordinates ->
                    val positionInParent = coordinates.positionInParent()
                    val size = coordinates.size

                    menuOffset = Offset(
                        positionInParent.x + size.width - 100,
                        positionInParent.y + size.height + with(density) { 8.dp.toPx() }
                    )
                }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPadding)
            ) {
                Text(
                    text = data.title ?: "",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.content ?: "",
                    style = TextStyle(fontSize = 14.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(data.createdAt.toString()),
                    style = TextStyle(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (showMenu) {
                val dpOffset = with(density) {
                    DpOffset(menuOffset.x.toDp(), menuOffset.y.toDp())
                }

                DropdownMenu(
                    modifier = Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    ),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    offset = dpOffset,
                    shape = RoundedCornerShape(10.dp),
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 4.dp
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            onItemDelete(data)
                        },
                        text = {
                            Row {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.Delete,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = "Delete"
                                )
                                Spacer(Modifier.padding(horizontal = 4.dp))
                                Text(
                                    color = MaterialTheme.colorScheme.error,
                                    text = stringResource(id = R.string.delete)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun CreatingNoteState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.creating_note),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResponsiveToast(
    message: String,
    type: ToastType,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        CustomToast(
            message = message,
            type = type,
            duration = 2000L,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.no_notes),
            style = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
