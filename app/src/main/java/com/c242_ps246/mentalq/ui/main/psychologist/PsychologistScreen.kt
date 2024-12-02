package com.c242_ps246.mentalq.ui.main.psychologist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.c242_ps246.mentalq.data.remote.response.PsychologistItem
import com.c242_ps246.mentalq.ui.theme.MentalQTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsychologistScreen(
//    viewModel: PsychologistViewModel = hiltViewModel()
) {
//    val uiState by viewModel.uiState.collectAsState()
//    val psychologistList by viewModel.psychologists.collectAsState()

    val psychologistList: List<PsychologistItem> = listOf(
        PsychologistItem(
            id = 888,
            name = "Dr. John Doe",
            email = "alice.smith@example.com",
            birthday = "1980-05-12",
            profilePhotoUrl = "https://randomuser.me/api/portraits/men/75.jpg",
            role = "psychologist"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Psychologist") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
            ) {
                items(
                    items = psychologistList,
                    key = { it.id }
                ) { psychologist ->
                    PsychologistCard(psychologist = psychologist)
                }
            }
        }
    }
}

@Composable
private fun PsychologistCard(
    psychologist: PsychologistItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {}),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = psychologist.profilePhotoUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.clip(
                            CircleShape
                        )
                    )
                    Text(
                        text = psychologist.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

        }
    }
}


@Preview
@Composable
fun PreviewPsychologistScreen() {
    MentalQTheme {
        PsychologistScreen()
    }
}