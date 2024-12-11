package com.c242_ps246.mentalq.ui.main.chat

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.data.remote.response.ChatRoomItem
import com.c242_ps246.mentalq.ui.component.EmptyState
import com.c242_ps246.mentalq.ui.utils.Utils.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToChatRoom: (String) -> Unit,
    onBackClick: () -> Unit
) {

    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chatRooms by viewModel.chatRooms.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()

    BackHandler { onBackClick() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.your_messages)) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Log.e("ChatScreen", "$chatRooms")
            if (chatRooms.isEmpty() && !uiState.isLoading) {
                Log.e("ChatScreen", "ChatScreen: Its Empty!")
                EmptyState(
                    title = stringResource(R.string.no_messages),
                    subtitle = stringResource(R.string.no_messages_desc)
                )
            } else {

                Log.d("Lazy", "$chatRooms")

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = chatRooms,
                        key = { it.id }
                    ) { chatRoom ->
                        ChatPreviewItem(
                            chatRoom = chatRoom,
                            currentUserId = userId!!,
                            onClick = { onNavigateToChatRoom(chatRoom.id) }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ChatPreviewItem(
    chatRoom: ChatRoomItem,
    currentUserId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        tonalElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            val profileImg = if (currentUserId == chatRoom.userId) {
                chatRoom.psychologistProfile
            } else {
                chatRoom.userProfile
            }

            AsyncImage(
                model = if (profileImg == "null" || profileImg == null) {
                    Log.e("ChatScreen", "Null")
                    R.drawable.default_profile
                } else {
                    Log.e("ChatScreen", "Ada")
                    profileImg
                },
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val title = if (currentUserId == chatRoom.userId) {
                        val prefix = chatRoom.psychologistPrefix
                        val suffix = chatRoom.psychologistSuffix

                        val prefixTitle = if (prefix != "null") "$prefix " else ""
                        val suffixTitle = if (suffix != "null") " $suffix" else ""

                        "$prefixTitle${chatRoom.psychologistName}$suffixTitle"
                    } else {
                        chatRoom.userName
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatTimestamp(chatRoom.updatedAt.toLong()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Log.e("ChatScreen", "ChatPreviewItem: ${chatRoom.lastMessageSenderId}")

                    val lastMassage = if (currentUserId == chatRoom.lastMessageSenderId) {
                        stringResource(R.string.you_chat) + ": ${chatRoom.lastMessage}"
                    } else {
                        chatRoom.lastMessage
                    }

                    Text(
                        text = lastMassage ?: "No messages yet",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}