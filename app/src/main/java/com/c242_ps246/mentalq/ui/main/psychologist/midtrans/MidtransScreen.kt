package com.c242_ps246.mentalq.ui.main.psychologist.midtrans

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.ui.component.EmptyState
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun MidtransScreen(
    orderId: String,
    itemId: String,
    userId: String,
    onSuccess: (String) -> Unit,
    onFailed: () -> Unit,
    onBackClick: () -> Unit
) {

    Log.e("MidtransScreen", "MidtransScreen: $orderId, $itemId, $userId")

    val viewModel: MidtransViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getTransactionStatus(orderId)
        viewModel.getPsychologistData(itemId)
        viewModel.getUserDataById(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val transactionStatus by viewModel.transactionStatus.collectAsState()
    val transactionMessage by viewModel.transactionMessage.collectAsState()
    val psychologistData by viewModel.psychologistData.collectAsState()
    val userData by viewModel.userData.collectAsState()

    val isAlreadyCreated = remember { mutableStateOf(false) }

    BackHandler {
        onBackClick()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
//            Log.e("MidtransScreen", "MidtransScreen: $transactionStatus, $transactionMessage")
            if (transactionStatus == "settlement") {

                val isDataNotNull = psychologistData != null && userData != null

                if (isDataNotNull) {
                    if (!isAlreadyCreated.value) {
                        isAlreadyCreated.value = true
                        Log.e("MidtransScreen", "MidtransScreen: AKU KEPANGGIL")


                        makeNewChatRoom(
                            userId = userId,
                            userName = userData!!.name,
                            userProfile = userData!!.profilePhotoUrl,
                            psychologistId = itemId,
                            psychologistName = psychologistData!!.users.name,
                            psychologistProfile = psychologistData!!.users.profilePhotoUrl,
                            onSuccess = onSuccess
                        )
                    }
                }


            } else {
                Log.e("MidtransScreen", "MidtransScreen: $psychologistData")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.transaction_failed))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.transaction_failed_desc))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.cancelTransaction(orderId)
                        onFailed()
                    }) {
                        Text(stringResource(R.string.back))
                    }

                }


            }
        }


    }
}

private fun makeNewChatRoom(
    userId: String,
    userName: String,
    userProfile: String?,
    psychologistId: String,
    psychologistName: String,
    psychologistProfile: String?,
    onSuccess: (String) -> Unit
) {
    val firebase = Firebase.database
    val chatRef = firebase.reference.child("chatroom").push()
    val chatId = chatRef.key

//    val members = listOf(userId, psychologistId)

    val members = mapOf(
        "user" to mapOf(
            "id" to userId,
            "name" to userName,
            "profile" to userProfile
        ),
        "psychologist" to mapOf(
            "id" to psychologistId,
            "name" to psychologistName,
            "profile" to psychologistProfile
        )
    )


    val initialData = hashMapOf(
        "lastMessageSenderId" to "",
        "lastMessage" to "",
        "members" to members,
        "psychologistId" to psychologistId,
        "createdAt" to System.currentTimeMillis().toString(),
        "updatedAt" to System.currentTimeMillis().toString(),
        "isEnded" to false
    )

    chatRef.setValue(initialData).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            listOf(userId, psychologistId).forEach { memberId ->
                val userChatsRef = firebase.reference.child("userChats").child(memberId)
                userChatsRef.push().setValue(chatId).addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess(chatId!!)
                    }
                }
            }
//
//            members.forEach { userId ->
//                val userChatsRef = firebase.reference.child("userChats").child(userId)
//                userChatsRef.push().setValue(chatId).addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        onSuccess(chatId!!)
//                    }
//                }
//            }
        }
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MidtransWebView(
    onBackClick: (String?) -> Unit,
    userId: String,
    price: Int,
    itemId: String
) {


    val viewModel: MidtransViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.createTransaction(price, itemId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val orderId by viewModel.orderId.collectAsState()
    val redirectUrl by viewModel.redirectUrl.collectAsState()

    Log.e("MidtransWebView", "MidtransWebView: $userId, $price, $itemId, $orderId")

    if (!uiState.isLoading) {
        BackHandler { onBackClick(orderId) }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            AndroidView(factory = {
                WebView(it).apply {
                    settings.javaScriptEnabled = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val currentUrl = request?.url.toString()
                            if (currentUrl != redirectUrl) {
                                onBackClick(orderId)
                            }
                            return super.shouldOverrideUrlLoading(view, request)
                        }
                    }
                    loadUrl(redirectUrl!!)
                }
            }, modifier = Modifier.fillMaxSize())
        }


    }

}