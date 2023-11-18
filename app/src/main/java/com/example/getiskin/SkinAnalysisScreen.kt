package com.example.getiskin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
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
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
import androidx.navigation.compose.rememberNavController
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SkinAnalysisScreen(navController: NavController) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectUris by remember { mutableStateOf<MutableList<Uri?>?>(mutableListOf()) }
    val predictOliyList by remember { mutableStateOf<MutableList<Int>>(mutableListOf()) }
    val predictFaceList by remember { mutableStateOf<MutableList<Int>>(mutableListOf()) }
    val scope = rememberCoroutineScope()
    val maxUrisSize = 3
    //카메라 퍼미션 확인
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    //카메라 퍼미션 확인 런쳐
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            hasCameraPermission = true
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to take photos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //카메라로 찍은 파일 Uri로 바꿔줌
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

    //카메라 런쳐
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // 사진 촬영 성공, imageUri에 이미지가 저장됨
            scope.launch {
                imageUri?.let { uri ->
                    //이미지 uri들을 selectUris에 하나씩 저장
                    selectUris?.let { uris ->
                        val newList = uris.toMutableList()
                        newList.add(uri)
                        selectUris = if (newList.size > maxUrisSize) {
                            newList.takeLast(maxUrisSize).toMutableList()
                        } else {
                            newList
                        }
                    }
                    //파일로 변경후 서버 모델에서 예측값 받아오기
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val file = File(context.cacheDir, "image.png")
                    inputStream?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val (predictedClassOliy, predictedClassFace) = uploadImage(file)
                    predictOliyList.add(predictedClassOliy)
                    predictFaceList.add(predictedClassFace)
                }
            }
        } else {
            // 사진 촬영 실패 처리
            // TODO: 사용자에게 촬영 실패를 알리는 메시지를 표시합니다.
        }
    }

    //포토피커로 사진 여러장 가져오기
    val multiPhotoLoader = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
//            selectUris = uris.toMutableList()
            val selectedUris = uris.take(3)
            // 현재의 selectUris 크기가 3을 초과하는 경우, 앞에서부터 제거
            while (selectUris!!.size + selectedUris.size > 3) {
                selectUris!!.removeAt(0)
            }
            selectUris!!.addAll(selectedUris)

            scope.launch {
                selectUris.let {
                    if (it != null) {
                        for (uri in it) {
                            if (uri != null) {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val file = File(context.cacheDir, "image.png")
                                inputStream?.use { input ->
                                    file.outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                try {
                                    val (predictedClassOliy, predictedClassFace) = uploadImage(file)
                                    predictOliyList.add(predictedClassOliy)
                                    predictFaceList.add(predictedClassFace)
                                    Log.d("성공함", "예측값 추가됨: $predictedClassOliy, $predictedClassFace")
                                } catch (e: Exception) {
                                    Log.e("예외 발생", e.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor("#ffffff")))
    ) {
        // 상단 이미지
        Image(
            painter = painterResource(id = R.drawable.logo), // 이미지 리소스 ID로 변경
            contentDescription = null, // contentDescription은 필요에 따라 추가
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 이미지 높이 조절
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            ) {
            Spacer(modifier = Modifier.height(80.dp))
            // Text
            Text(
                text = "피 부 측 정",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(android.graphics.Color.parseColor("#F7F1E5")))
                    .padding(20.dp)
                    .height(30.dp), // 여백 추가
                textAlign = TextAlign.Center,
                fontSize = 24.sp, // 원하는 크기로 조절
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#e39368")) // 원하는 색상으로 조절
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                // 사진 보여주는곳
                Row {
                    for (uri in selectUris.orEmpty()) {
                        if (uri != null) {
                            val headBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                val decodeBitmap = ImageDecoder.decodeBitmap(
                                    ImageDecoder.createSource(
                                        context.contentResolver, uri
                                    )
                                )
                                decodeBitmap
                            } else {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            }
                            Image(
                                bitmap = headBitmap.asImageBitmap(), contentDescription = "", modifier = Modifier
                                    .size(130.dp)
                                    .shadow(2.dp)
                                    .clickable {
                                        selectUris?.let { currentUris ->
                                            // 클릭한 이미지의 uri를 제거
                                            val updatedUris = currentUris
                                                .filter { it != uri }
                                                .toMutableList()
                                            selectUris = updatedUris
                                        }
                                    }
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        if (hasCameraPermission) {
                            val uri = createImageUri()
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp)
                        .border(
                            1.dp,
                            Color(android.graphics.Color.parseColor("#e39368")),
                            shape = RoundedCornerShape(10)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFFE39368), // 버튼 배경색상 설정
                        contentColor = White // 버튼 내부 텍스트 색상 설정
                    ),
                    shape = RoundedCornerShape(10)

                ) {
                    Text(
                        text = "카메라로 촬영하기",
                        textAlign = TextAlign.Center,
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        // 앨범 런처를 실행합니다.
                        multiPhotoLoader.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp)
                        .border(
                            1.dp,
                            Color(android.graphics.Color.parseColor("#e39368")),
                            shape = RoundedCornerShape(10)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFFE39368), // 버튼 배경색상 설정
                        contentColor = White // 버튼 내부 텍스트 색상 설정
                    ),
                    shape = RoundedCornerShape(10)
                ) {
                    Text(
                        text = "앨범에서 사진 선택하기",
                        textAlign = TextAlign.Center,
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        val headEncodedUri = URLEncoder.encode(selectUris?.get(0).toString(), "UTF-8")
                        val noseEncodedUri = URLEncoder.encode(selectUris?.get(1).toString(), "UTF-8")
                        val cheekEncodedUri = URLEncoder.encode(selectUris?.get(2).toString(), "UTF-8")
                        if (selectUris != null) {
                            navController.navigate(
                                "results/${predictOliyList[0]}/${predictOliyList[1]}/${predictOliyList[2]}/${predictFaceList[0]}/${predictFaceList[1]}/${predictFaceList[2]}/${headEncodedUri}/${noseEncodedUri}/${cheekEncodedUri}"
                            )
                        }
                    },
                    enabled = (selectUris?.size ?: 0) >= 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "제출",
                        color = Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp)) // 간격 조절
                TextButton(
                    onClick = {
                        navController.navigate("home")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)

                ) {
                    Text(
                        text = "홈으로",
                        color = Gray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

suspend fun uploadImage(file: File): Pair<Int, Int> = withContext(Dispatchers.IO) {
    val url = "http://192.168.45.55:5000/predict"
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
            val intValue = Pair(predictResponse.predictedClassOliy, predictResponse.predictedClassFace)

            Log.d("성공함", "이미지가 올라갔다? Respones : ${responseBody ?: "no data"}")
            return@withContext intValue
        } else {
            Log.e("망함", "망함")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle exception
    } as Pair<Int, Int>
}

data class PredictResponse(
    @SerializedName("predicted_class_oliy")
    val predictedClassOliy: Int,
    @SerializedName("predicted_class_face")
    val predictedClassFace: Int,
)


//Column(
//modifier = Modifier
//.fillMaxWidth()
//.padding(16.dp),
//verticalArrangement = Arrangement.Bottom
//) {
//    Spacer(modifier = Modifier.height(16.dp)) // 간격 조절
//
//    TextButton(
//        onClick = {
//             "홈으로" 버튼 클릭 시 수행할 작업 추가
//             예를 들어, 홈 화면으로 이동하는 네비게이션 등
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(50.dp)
//            .background(MaterialTheme.colorScheme.primary)
//    ) {
//        Text(
//            text = "홈으로",
//            color = White,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}

@Preview
@Composable
fun SkinAnalysisScreenPreview() {
    // You can create a preview of the HomeScreen here
    // For example, you can use a NavController with a LocalCompositionLocalProvider
    // to simulate the navigation.
    // Note: This is a simplified example; you may need to adjust it based on your actual navigation setup.
    val navController = rememberNavController()
    SkinAnalysisScreen(navController = navController)
}
