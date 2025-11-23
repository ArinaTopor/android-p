package com.example.app.screens

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.outlined.AccessTime
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app.receivers.PairNotificationReceiver
import com.example.app.viewmodels.ProfileViewModel
import com.example.app.data.repository.UserProfile
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val current by viewModel.profile.collectAsState()
    val context = LocalContext.current
    var fullName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var position by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var resumeUrl by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var avatarUri by rememberSaveable { mutableStateOf("") }
    var favoritePairTime by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var timeError by remember { mutableStateOf<String?>(null) }

    fun validateTime(time: String, showError: Boolean = true): Boolean {
        if (time.isEmpty()) {
            if (showError) timeError = "Поле не заполнено"
            return false
        }
        val pattern = Regex("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")
        if (!pattern.matches(time)) {
            if (showError) timeError = "Неверный формат времени. Используйте HH:mm"
            return false
        }
        if (showError) timeError = null
        return true
    }

    LaunchedEffect(current) {
        val fieldsAreBlank =
            fullName.text.isEmpty() && position.text.isEmpty() && resumeUrl.text.isEmpty() && avatarUri.isEmpty() && favoritePairTime.text.isEmpty()
        if (fieldsAreBlank) {
            fullName = TextFieldValue(current.fullName)
            position = TextFieldValue(current.position)
            resumeUrl = TextFieldValue(current.resumeUrl)
            avatarUri = current.avatarUri
            favoritePairTime = TextFieldValue(current.favoritePairTime)
            if (current.favoritePairTime.isNotEmpty()) {
                validateTime(current.favoritePairTime)
            }
        }
    }

    LaunchedEffect(favoritePairTime.text) {
        validateTime(favoritePairTime.text)
    }


    var showSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { avatarUri = it.toString() }
        }

    val takePicture =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                pendingCameraUri?.let { avatarUri = it.toString() }
            }
        }

    val requestPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        showSourceDialog = true
    }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun ensurePermissionsAndChoose(source: String) {
        val needed = mutableListOf<String>()
        if (source == "gallery") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    needed += Manifest.permission.READ_MEDIA_IMAGES
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    needed += Manifest.permission.READ_EXTERNAL_STORAGE
                }
            }
        } else if (source == "camera") {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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

    fun onTimeChanged(newValue: TextFieldValue) {
        favoritePairTime = newValue
        validateTime(newValue.text)
    }

    fun showTimePicker() {
        val currentTime = favoritePairTime.text
        val initialHour: Int
        val initialMinute: Int

        if (currentTime.isNotEmpty() && currentTime.matches(Regex("^([0-1][0-9]|2[0-3]):[0-5][0-9]$"))) {
            val parts = currentTime.split(":")
            initialHour = parts[0].toIntOrNull() ?: 0
            initialMinute = parts[1].toIntOrNull() ?: 0
        } else {
            val calendar = Calendar.getInstance()
            initialHour = calendar.get(Calendar.HOUR_OF_DAY)
            initialMinute = calendar.get(Calendar.MINUTE)
        }

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                onTimeChanged(TextFieldValue(timeString))
            },
            initialHour,
            initialMinute,
            true
        )
        timePickerDialog.show()
    }

    fun cancelNotification() {
        try {
            val intent = Intent(context, PairNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleNotification(userName: String, time: String) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }

            if (!notificationManager.areNotificationsEnabled()) {
                return
            }

            cancelNotification()

            val (hours, minutes) = time.split(":").map { it.toInt() }
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val intent = Intent(context, PairNotificationReceiver::class.java).apply {
                putExtra(PairNotificationReceiver.EXTRA_USER_NAME, userName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerTime = calendar.timeInMillis

            if (alarmManager == null) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val showIntent = Intent(context, com.example.app.MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val showPendingIntent = PendingIntent.getActivity(
                    context,
                    1,
                    showIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isFormValid = remember(favoritePairTime.text, timeError) {
        if (favoritePairTime.text.isEmpty()) {
            false
        } else {
            timeError == null && validateTime(favoritePairTime.text, showError = false)
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
                Button(
                    onClick = {
                        val timeText = favoritePairTime.text.trim()
                        if (validateTime(timeText)) {
                            val profile = UserProfile(
                                fullName = fullName.text.trim(),
                                position = position.text.trim(),
                                avatarUri = avatarUri,
                                resumeUrl = resumeUrl.text.trim(),
                                favoritePairTime = timeText
                            )
                            viewModel.updateProfile(profile)

                            if (timeText.isNotEmpty() && fullName.text.trim().isNotEmpty()) {
                                scheduleNotification(fullName.text.trim(), timeText)
                            } else if (timeText.isEmpty()) {
                                cancelNotification()
                            }

                            navController.popBackStack()
                        }
                    },
                    enabled = isFormValid
                ) { Text("Готово") }
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

            OutlinedTextField(
                value = favoritePairTime,
                onValueChange = { onTimeChanged(it) },
                label = { Text("Время любимой пары") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showTimePicker() }) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = "Выбрать время"
                        )
                    }
                },
                isError = timeError != null,
                supportingText = {
                    if (timeError != null) {
                        Text(
                            text = timeError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
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
                    Button(onClick = {
                        showSourceDialog = false; ensurePermissionsAndChoose("gallery")
                    }, modifier = Modifier.fillMaxWidth()) { Text("Из галереи") }
                    Button(onClick = {
                        showSourceDialog = false; ensurePermissionsAndChoose("camera")
                    }, modifier = Modifier.fillMaxWidth()) { Text("С камеры") }
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

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingChannel =
            notificationManager.getNotificationChannel(PairNotificationReceiver.CHANNEL_ID)
        if (existingChannel != null) {
            return
        }

        val channel = NotificationChannel(
            PairNotificationReceiver.CHANNEL_ID,
            "Уведомления о парах",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомления о начале любимой пары"
            enableLights(true)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}

