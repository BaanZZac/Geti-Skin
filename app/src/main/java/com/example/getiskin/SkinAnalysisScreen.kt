package com.example.getiskin

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
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SkinAnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var predict by remember { mutableStateOf<Any?>("") }
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            hasCameraPermission = true
        } else {
            Toast.makeText(context, "Camera permission is required to take photos", Toast.LENGTH_LONG).show()
        }
    }

    // 파일 URI 생성을 위한 함수
    fun createImageUri(): Uri {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
        ).also { uri ->
            imageUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // 사진 촬영 성공, imageUri에 이미지가 저장됨
            scope.launch {
                imageUri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val file = File(context.cacheDir, "image.png")
                    inputStream?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    predict = uploadImage(file)
                }
            }
        } else {
            // 사진 촬영 실패 처리
            // TODO: 사용자에게 촬영 실패를 알리는 메시지를 표시합니다.
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
            scope.launch {
                imageUri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val file = File(context.cacheDir, "image.png")
                    inputStream?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    predict = uploadImage(file)
                    // predict 값을 업데이트하고, 결과가 오기를 기다립니다.
//                    predict = withContext(Dispatchers.IO) {
//                        uploadImage(file)
//                    }

                    // 업데이트된 predict 값을 사용하여 navigate 합니다.
//                    navController.navigate("results/${predict.toString().toInt()}")
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (hasCameraPermission) {
                val uri = createImageUri()
                cameraLauncher.launch(uri)
            } else {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
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
            Text(text = "선택된 이미지 URI: $predict")
            Button(onClick = { navController.navigate("results/${predict.toString().toInt()}") }) {
                Text(text = "진단하기")
            }
        }
    }
}

suspend fun uploadImage(file: File) = withContext(Dispatchers.IO) {
    val url = "http://192.168.1.111:5000/predict"
    val client = OkHttpClient()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "image",
            "image.png",
            RequestBody.create(MediaType.parse("image/*"), file)
        )
        .build()

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    try {
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body()?.string()

            val gson = Gson()
            val predictResponse = gson.fromJson(responseBody, PredictResponse::class.java)
            val intValue = predictResponse.predictedClass

            Log.d("성공함", "이미지가 올라갔다? Respones : ${responseBody ?: "no data"}")
            return@withContext intValue
        } else {
            Log.e("망함", "망함")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle exception
    }
}


data class PredictResponse(
    @SerializedName("predicted_class")
    val predictedClass: Int
)