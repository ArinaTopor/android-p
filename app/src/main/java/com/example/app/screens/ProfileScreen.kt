package com.example.app.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    IconButton(onClick = { navController.navigate("profile/edit") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
			if (profile.avatarUri.isNotEmpty()) {
				AsyncImage(
					model = Uri.parse(profile.avatarUri),
					contentDescription = "Avatar",
					modifier = Modifier
						.size(120.dp)
						.clip(CircleShape),
					contentScale = ContentScale.Crop
				)
			} else {
				Box(
					modifier = Modifier
						.size(120.dp)
						.clip(CircleShape)
						.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
						.background(MaterialTheme.colorScheme.surfaceVariant),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Outlined.AccountCircle,
						contentDescription = "Нет фото",
						tint = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}

            Text(
                text = if (profile.fullName.isNotEmpty()) profile.fullName else "(Нет информации. Заполните её в настройках профиля )",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            if (profile.position.isNotEmpty()) {
                Text(
                    text = profile.position,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                enabled = profile.resumeUrl.isNotEmpty(),
                onClick = { downloadResume(context, profile.resumeUrl) }
            ) {
                Text("Резюме")
            }
        }
    }
}

private fun downloadResume(context: Context, url: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Резюме")
            .setDescription("Загрузка документа")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "resume.pdf")
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    } catch (_: Exception) {
    }
}

