package com.c242_ps246.mentalq.ui.main.psychologist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.data.remote.response.PsychologistItem
import com.c242_ps246.mentalq.ui.component.EmptyState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistScreen(
    onBackClick: () -> Unit,
    onNavigateToMidtransWebView: (String, Int, String) -> Unit,
    viewModel: PsychologistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val psychologistList by viewModel.psychologists.collectAsState()
    val userId by viewModel.userId.collectAsStateWithLifecycle()

    BackHandler {
        onBackClick()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.psychologist_list)) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {

                if (psychologistList.isNullOrEmpty()) {
                    EmptyState(
                        title = stringResource(id = R.string.no_psychologist_found),
                        subtitle = stringResource(id = R.string.no_psychologist_found_subtitle),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        items(
                            items = psychologistList!!,
                            key = { it.id }
                        ) { psychologist ->
                            PsychologistCard(
                                psychologist = psychologist,
                                userId = userId!!,
                                onNavigateToMidtransWebView = onNavigateToMidtransWebView
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PsychologistCard(
    psychologist: PsychologistItem,
    modifier: Modifier = Modifier,
    userId: String,
    onNavigateToMidtransWebView: (String, Int, String) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onNavigateToMidtransWebView(userId, psychologist.price, psychologist.userId)
            }),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val imageModel =
                        psychologist.users.profilePhotoUrl ?: R.drawable.default_profile

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageModel)
                            .placeholder(R.drawable.default_profile)
                            .crossfade(true)
                            .size(64)
                            .build(),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    val locale = Locale("id", "ID")
                    val formatter = NumberFormat.getCurrencyInstance(locale)

                    Column {
                        Text(
                            text = "${psychologist.prefixTitle} ${psychologist.users.name} ${psychologist.suffixTitle}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = formatter.format(psychologist.price),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}