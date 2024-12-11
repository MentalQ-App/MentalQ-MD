package com.c242_ps246.mentalq.ui.main.chat

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.ui.utils.Utils.formatTimestamp
import com.c242_ps246.mentalq.data.remote.response.ChatMessageItem
import com.c242_ps246.mentalq.ui.component.CustomDialog
import kotlinx.coroutines.delay

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
    val profileUrl by viewModel.profileUrl.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val isEnded by viewModel.isEnded.collectAsState()



    BackHandler {
        onBackClick()
    }

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.getMessages(chatRoomId)
            viewModel.getProfileUrl(chatRoomId, it)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.getSessionStatus(chatRoomId)
            delay(5000L)
        }
    }

    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        userId?.let { nonNullUserId ->
            Scaffold(
                topBar = {
                    ChatTopBar(
                        profileUrl = profileUrl,
                        userName = userName.orEmpty(),
                        onBackClick = onBackClick
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ChatMessages(
                        messages = messages,
                        currentUserRole = userRole.orEmpty(),
                        currentUserId = nonNullUserId,
                        isEnded = isEnded,
                        chatRoomId = chatRoomId,
                        onSendMessage = { message ->
                            viewModel.sendMessage(
                                chatRoomId = chatRoomId,
                                userId = nonNullUserId,
                                messageText = message
                            )
                        }
                    )
                }
            }
        } ?: run {
            Text("User ID is null. Cannot load chat.")
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    profileUrl: String?,
    userName: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets(
            top = 4.dp,
            bottom = 4.dp
        ),
        title = {
            Row(
                modifier = Modifier
                    .wrapContentSize()
            ) {

                val profile = if (profileUrl.isNullOrEmpty() || profileUrl == "null") {
                    R.drawable.default_profile
                } else {
                    profileUrl
                }

                Log.e("ChatRoomScreen", "ChatTopBar: '$profileUrl'")

                AsyncImage(
                    model = profile,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape),
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = userName
                )

                Log.e("ChatRoomScreen", "ChatTopBar: $userName, $profileUrl")
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

        }
    )
}

@Composable
fun ChatMessages(
    messages: List<ChatMessageItem>,
    currentUserRole: String,
    isEnded: Boolean,
    chatRoomId: String,
    currentUserId: String,
    onSendMessage: (String) -> Unit
) {

    val hideKeyboard = LocalSoftwareKeyboardController.current

    val viewModel: ChatRoomViewModel = hiltViewModel()

    val messageText = remember {
        mutableStateOf("")
    }

    val isShowDialog = remember {
        mutableStateOf(false)
    }


    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(1f),
                state = listState
            ) {
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
                    .padding(horizontal = 8.dp)
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                if (isEnded) {
                    Text(
                        text = "Session has ended",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    TextField(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .weight(0.8f),
                        value = messageText.value,
                        onValueChange = {
                            if (it.length <= 1000) {
                                messageText.value = it
                            }
                        },
                        singleLine = false,
                        placeholder = { Text("Type a message") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                hideKeyboard?.hide()
                            }
                        ),
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
                            ),
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

        if (currentUserRole == "psychologist" && !isEnded) {
            Button(
                onClick = {
                    isShowDialog.value = true
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                elevation = ButtonDefaults.buttonElevation(10.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "End session")
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(id = R.string.end_session))
            }
        }

        if (isShowDialog.value) {
            CustomDialog(
                dialogTitle = stringResource(id = R.string.end_session),
                dialogMessage = stringResource(id = R.string.end_session_message),
                confirmColor = MaterialTheme.colorScheme.errorContainer,
                confirmTextColor = MaterialTheme.colorScheme.onErrorContainer,
                onDismiss = {
                    isShowDialog.value = false
                },
                onConfirm = {
                    isShowDialog.value = false
                    viewModel.endSession(chatRoomId)
                }
            )
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
            Column {
                Text(
                    text = message.content,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
                )

                Text(
                    text = formatTimestamp(message.createdAt!!.toLong()),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.End)
                )


            }

        }

    }
}
