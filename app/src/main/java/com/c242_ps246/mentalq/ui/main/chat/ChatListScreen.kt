package com.c242_ps246.mentalq.ui.main.chat
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil.compose.AsyncImage
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatListScreen(
//    onChatSelected: (String) -> Unit,
//    viewModel: ChatListViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//    val chatPreviews by viewModel.chatPreviews.collectAsStateWithLifecycle()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Messages") }
//            )
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            if (chatPreviews.isEmpty() && !uiState.isLoading) {
//                EmptyState()
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(vertical = 8.dp)
//                ) {
//                    items(
//                        items = chatPreviews,
//                        key = { it.recipientId }
//                    ) { chatPreview ->
//                        ChatPreviewItem(
//                            chatPreview = chatPreview,
//                            onClick = { onChatSelected(chatPreview.recipientId) }
//                        )
//                    }
//                }
//            }
//
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun ChatPreviewItem(
//    chatPreview: ChatPreview,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        modifier = modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        tonalElevation = 0.dp
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            AsyncImage(
//                model = chatPreview.recipient.profilePhotoUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(
//                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = chatPreview.recipient.name,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Text(
//                        text = formatTimestamp(chatPreview.timestamp),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = chatPreview.lastMessage ?: "No messages yet",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.weight(1f)
//                    )
//                    if (chatPreview.unreadCount > 0) {
//                        Badge(
//                            containerColor = MaterialTheme.colorScheme.primary
//                        ) {
//                            Text(
//                                text = chatPreview.unreadCount.toString(),
//                                color = MaterialTheme.colorScheme.onPrimary
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun EmptyState() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "No messages yet",
//            style = MaterialTheme.typography.titleMedium
//        )
//        Text(
//            text = "When you start a conversation, it will appear here",
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            textAlign = TextAlign.Center
//        )
//    }
//}
//
//private fun formatTimestamp(timestamp: Long): String {
//    val now = System.currentTimeMillis()
//    val diff = now - timestamp
//
//    return when {
//        diff < 24 * 60 * 60 * 1000 -> SimpleDateFormat("HH:mm", Locale.getDefault())
//        diff < 7 * 24 * 60 * 60 * 1000 -> SimpleDateFormat("EEE", Locale.getDefault())
//        else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault())
//    }.format(Date(timestamp))
//}