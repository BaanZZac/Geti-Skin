package com.example.getiskin

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// 필요한 추가 import 문
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SkinAnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher: ActivityResultLauncher<Uri>
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // 선택한 사진의 URI를 처리합니다.
        imageUri = uri
    }

    // 파일 URI 생성을 위한 함수
    fun createImageUri(): Uri {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return FileProvider.getUriForFile(
            context,
            "com.example.getiskin.fileprovider",  // 귀하의 앱의 fileprovider
            File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
        ).also { uri ->
            imageUri = uri  // 사진 촬영 후 이미지 URI 저장
        }
    }

    cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // 사진 촬영 성공, imageUri에 이미지가 저장됨
            // 여기에서 imageUri를 사용하여 이미지를 표시하거나 처리합니다.
        } else {
            // 사진 촬영 실패 처리
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // 카메라 런처를 실행합니다.
            val uri = createImageUri()
            cameraLauncher.launch(uri)
        }) {
            Text(text = "카메라로 촬영하기")
        }

        Button(onClick = {
            // 앨범 런처를 실행합니다.
            galleryLauncher.launch("image/*")
        }) {
            Text(text = "앨범에서 사진 선택하기")
        }

        // 선택한 사진의 URI를 화면에 표시합니다. (선택적)
        imageUri?.let { uri ->
            Text(text = "선택된 이미지 URI: $uri")
        }
    }
}
