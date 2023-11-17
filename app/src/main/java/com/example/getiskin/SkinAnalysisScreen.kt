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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
    var predictOliy by remember { mutableStateOf<Int?>(0) }
    var predictFace by remember { mutableStateOf<Int?>(0) }
    val scope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
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
                    val (predictedClassOliy, predictedClassFace) = uploadImage(file)

                    // Log 등을 통해 확인
                    Log.d(
                        "성공함",
                        "predictedClassOliy: $predictedClassOliy, predictedClassFace: $predictedClassFace"
                    )

                    // 이후에 각 값을 사용할 수 있도록 처리
                    predictOliy = predictedClassOliy
                    predictFace = predictedClassFace
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
                    val (predictedClassOliy, predictedClassFace) = uploadImage(file)

                    // Log 등을 통해 확인
                    Log.d(
                        "성공함",
                        "predictedClassOliy: $predictedClassOliy, predictedClassFace: $predictedClassFace"
                    )

                    // 이후에 각 값을 사용할 수 있도록 처리
                    predictOliy = predictedClassOliy
                    predictFace = predictedClassFace
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
                        galleryLauncher.launch("image/*")
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

                // 선택한 사진의 URI를 화면에 표시합니다. (선택적)
                imageUri?.let { uri ->
                    val encodedUri = URLEncoder.encode(uri.toString(), "UTF-8")
                    if (predictOliy == 1) {
                        Text(text = "지성 : $predictOliy")
                    } else {
                        Text(text = "건성 : $predictOliy")
                    }
                    Text(text = "이마 코 볼: $predictFace")
                    //버전이 낮은건 else를 실행한다
                    //ImageDecoder쪽을 복사하고 else todo랑 같이 검색하면됨
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val decodeBitmap = ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                context.contentResolver,
                                uri
                            )
                        )
                        decodeBitmap
                    }else {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(), contentDescription = "",
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(2.dp)
                    )
                    Button(
                        onClick = { navController.navigate("results/${predictOliy}/${predictFace}/${encodedUri}") },
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
                }
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
//            val intValue = Pair(predictResponse.predictedClassOliy, predictResponse.predictedClassFace)

            Log.d("성공함", "이미지가 올라갔다? Respones : ${responseBody ?: "no data"}")
            return@withContext Pair(
                predictResponse.predictedClassOliy,
                predictResponse.predictedClassFace
            )
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
    val predictedClassFace: Int
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
