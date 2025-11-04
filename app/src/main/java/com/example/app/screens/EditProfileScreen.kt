package com.example.app.screens

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app.viewmodels.ProfileViewModel
import com.example.app.data.repository.UserProfile
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val current by viewModel.profile.collectAsState()
    var fullName by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var position by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var resumeUrl by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var avatarUri by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(current) {
        val fieldsAreBlank = fullName.text.isEmpty() && position.text.isEmpty() && resumeUrl.text.isEmpty() && avatarUri.isEmpty()
        if (fieldsAreBlank) {
            fullName = TextFieldValue(current.fullName)
            position = TextFieldValue(current.position)
            resumeUrl = TextFieldValue(current.resumeUrl)
            avatarUri = current.avatarUri
        }
    }

    val context = LocalContext.current

    var showSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { avatarUri = it.toString() }
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            pendingCameraUri?.let { avatarUri = it.toString() }
        }
    }

    val requestPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // if granted, re-open dialog so пользователь выберет источник снова
        showSourceDialog = true
    }

    fun ensurePermissionsAndChoose(source: String) {
        val needed = mutableListOf<String>()
        if (source == "gallery") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    needed += Manifest.permission.READ_MEDIA_IMAGES
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    needed += Manifest.permission.READ_EXTERNAL_STORAGE
                }
            }
        } else if (source == "camera") {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                needed += Manifest.permission.CAMERA
            }
        }
        if (needed.isNotEmpty()) {
            requestPermissions.launch(needed.toTypedArray())
        } else {
            if (source == "gallery") {
                galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val uri = createImageUri(context)
                pendingCameraUri = uri
                if (uri != null) takePicture.launch(uri)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = { navController.popBackStack() }) { Text("Отмена") }
                Button(onClick = {
                    viewModel.updateProfile(
                        UserProfile(
                            fullName = fullName.text.trim(),
                            position = position.text.trim(),
                            avatarUri = avatarUri,
                            resumeUrl = resumeUrl.text.trim()
                        )
                    )
                    navController.popBackStack()
                }) { Text("Готово") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
			if (avatarUri.isNotEmpty()) {
				AsyncImage(
					model = Uri.parse(avatarUri),
					contentDescription = "Avatar",
					modifier = Modifier
						.size(120.dp)
						.clip(CircleShape)
						.clickable { showSourceDialog = true },
					contentScale = ContentScale.Crop
				)
			} else {
				Column(
					modifier = Modifier
						.size(160.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Box(
						modifier = Modifier
							.size(120.dp)
							.clip(CircleShape)
							.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
							.background(MaterialTheme.colorScheme.surfaceVariant)
							.clickable { showSourceDialog = true },
						contentAlignment = Alignment.Center
					) {
						Icon(
							imageVector = Icons.Outlined.AddAPhoto,
							contentDescription = "Добавить фото",
							tint = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
					Text(
						text = "Добавить фото",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Должность (опционально)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("URL резюме (pdf)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            confirmButton = {},
            title = { Text("Выбор источника") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { showSourceDialog = false; ensurePermissionsAndChoose("gallery") }, modifier = Modifier.fillMaxWidth()) { Text("Из галереи") }
                    Button(onClick = { showSourceDialog = false; ensurePermissionsAndChoose("camera") }, modifier = Modifier.fillMaxWidth()) { Text("С камеры") }
                }
            }
        )
    }
}

private fun createImageUri(context: android.content.Context): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "avatar_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AndroidP")
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    } else {
        val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, "avatar_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", imageFile)
    }
}


