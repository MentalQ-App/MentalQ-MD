package com.c242_ps246.mentalq.ui.main.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    chatRoomId: String,
    onBackClick: () -> Unit
) {
    val viewModel: ChatRoomViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val messages by viewModel.messages.collectAsState()


    BackHandler {
        onBackClick()
    }

    LaunchedEffect(key1 = true) {
        viewModel.getMessages(chatRoomId)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            ChatMessages(
                messages = messages,
                currentUserId = userId!!,
                onSendMessage = { message ->
                    viewModel.sendMessage(
                        chatRoomId = chatRoomId,
                        messageText = message
                    )
                })
        }
    }


}

@Composable
fun ChatMessages(
    messages: List<ChatMessageItem>,
    currentUserId: String,
    onSendMessage: (String) -> Unit
) {

    val hideKeyboard = LocalSoftwareKeyboardController.current

    val messageText = remember {
        mutableStateOf("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(items = messages) {
                ChatBubble(
                    message = it,
                    currentUserId = currentUserId
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {

            TextField(modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .weight(0.8f),
                value = messageText.value,
                onValueChange = { messageText.value = it },
                placeholder = { Text("Type a message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboard?.hide()
                    }
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            val isClickable = messageText.value.isNotBlank()

            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isClickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                    .then(
                        if (isClickable) {
                            Modifier.clickable {
                                onSendMessage(messageText.value)
                                messageText.value = ""
                            }
                        } else {
                            Modifier
                        }
                    )
                    ,
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(24.dp)
                )
            }


        }

    }
}

@Composable
fun ChatBubble(
    message: ChatMessageItem,
    currentUserId: String
) {
    val isCurrentUser = message.senderId == currentUserId
    val color = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        horizontalArrangement = if (isCurrentUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        },
        verticalAlignment = Alignment.Bottom
    ) {

        Box(
            modifier = Modifier
                .padding(2.dp)
                .background(color = color, shape = RoundedCornerShape(16.dp))
        ) {
            Text(
                text = message.content,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
            )
        }

    }
}
