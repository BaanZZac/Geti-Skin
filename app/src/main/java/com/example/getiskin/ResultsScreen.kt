package com.example.getiskin

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


data class SkinAnalysisData(
    val userID: String,
    var timestamp: String,
    val finalSkinType: String,
    val skinType1: String,
    val skinType2: String,
    val skinType3: String,
    val facePart1: String,
    val facePart2: String,
    val facePart3: String,
    val imageUrl1: String,
    val imageUrl2: String,
    val imageUrl3: String,
) {
    // 매개변수가 없는 기본 생성자
    // firestore가 기본 생성자가 없으면 값을 못읽음... 생성필수라함
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "")
}

fun saveSkinAnalysisData(skinAnalysisData: SkinAnalysisData) {
    val db = FirebaseFirestore.getInstance()

    // Add a new document with a generated ID
    db.collection("skinAnalysis")
        .add(skinAnalysisData)
        .addOnSuccessListener { documentReference ->
            println("DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding document: $e")
        }
}

@Composable
fun HomeReturnButton2(
    modifier: Modifier,
    navController: NavController,
    auth: FirebaseAuth,
    uri1: Uri,
    uri2: Uri,
    uri3: Uri,
    skinType1: String,
    skinType2: String,
    skinType3: String,
    facePart1: String?,
    facePart2: String?,
    facePart3: String?,
    finalSkinType: String?
) {
    val user = auth.currentUser
    val uid = user?.uid ?: "" //유저
    var imageUrl1 by remember { mutableStateOf("") }
    var imageUrl2 by remember { mutableStateOf("") }
    var imageUrl3 by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                uploadRawDataToFirestorage(uri1, skinType1, facePart1,
                    onImageUploaded = {
                        Log.d("성공", "이미지 업로드 URL : $it")
                    })
                uploadRawDataToFirestorage(uri2, skinType2, facePart2,
                    onImageUploaded = {
                        Log.d("성공", "이미지 업로드 URL : $it")
                    })
                uploadRawDataToFirestorage(uri3, skinType3, facePart3,
                    onImageUploaded = {
                        Log.d("성공", "이미지 업로드 URL : $it")
                    })
                uploadImageToFirestorage(uri1) { url1 ->
                    imageUrl1 = url1
                    Log.d("URL", "이미지 업로드 URL : $url1")
                    uploadImageToFirestorage(uri2) { url2 ->
                        imageUrl2 = url2
                        uploadImageToFirestorage(uri3) { url3 ->
                            imageUrl3 = url3
                            val skinAnalysis = SkinAnalysisData(
                                uid,
                                timestamp,
                                finalSkinType!!,
                                skinType1,
                                skinType2,
                                skinType3,
                                facePart1!!,
                                facePart2!!,
                                facePart3!!,
                                imageUrl1,
                                imageUrl2,
                                imageUrl3
                            )
                            saveSkinAnalysisData(skinAnalysis)
                            navController.navigate("home")
                        }

                    }
                }
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "결과 저장 및 홈으로",
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

fun uploadImageToFirestorage(imageUri: Uri, onImageUploaded: (String) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageRef = storageRef.child("images/${imageUri.lastPathSegment}")

    imageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                val imageUrl = downloadUri.toString()
                onImageUploaded(imageUrl)
            }
        }
        .addOnFailureListener {
            // 이미지 업로드 실패 시 처리
            Log.d("망함", "망함")
        }
}

@Composable
fun AdPlaces() {
    var isClicked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(10.dp)
            .border(
                1.dp,
                Color(android.graphics.Color.parseColor("#e39368")),
                shape = RoundedCornerShape(10)
            )
            .clickable {
                // 광고를 클릭할 때 수행할 작업 추가
                isClicked = true
            }
    ) {
        if (isClicked) {
            // 클릭되었을 때의 UI
            // 예를 들어, 광고 클릭 후에 할 작업을 여기에 추가
            ShowToast(message = "광고가 나올 화면입니다.")
        }

        Image(
            painter = painterResource(id = R.drawable.analysis), // 가상 이미지 리소스 ID로 변경
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .border(
                    1.dp,
                    Color(android.graphics.Color.parseColor("#e39368")),
                    shape = RoundedCornerShape(10)
                )
        )
        // 광고 텍스트
        Text(
            text = "피부클리닉 샵 광고",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun ResultsScreen(
    navController: NavController,
    auth: FirebaseAuth,
    predictOilHead: Int?,
    predictOilNose: Int?,
    predictOilCheek: Int?,
    predictHead: Int?,
    predictNose: Int?,
    predictCheek: Int?,
    headUriString: String?,
    noseUriString: String?,
    cheekUriString: String?
) {
    val uri1 = Uri.parse(headUriString)
    val uri2 = Uri.parse(noseUriString)
    val uri3 = Uri.parse(cheekUriString)
    val skinType1 = if (predictOilHead == 0) "건성" else "지성"
    val skinType2 = if (predictOilNose == 0) "건성" else "지성"
    val skinType3 = if (predictOilCheek == 0) "건성" else "지성"
    val facePart1 = when (predictHead) {
        0 -> "볼"
        1 -> "이마"
        2 -> "코"
        else -> null // 또는 원하는 다른 처리를 수행할 수 있음
    }

    val facePart2 = when (predictNose) {
        0 -> "볼"
        1 -> "이마"
        2 -> "코"
        else -> null
    }

    val facePart3 = when (predictCheek) {
        0 -> "볼"
        1 -> "이마"
        2 -> "코"
        else -> null
    }
    var finalSkinType: String? = null

    if (predictOilHead != null && predictOilNose != null && predictOilCheek != null) {
        finalSkinType = when {
            predictOilHead > 3 && predictOilNose > 3 && predictOilCheek > 3 -> "건성"
            predictOilHead <= 3 && predictOilNose <= 3 && predictOilCheek <= 3 -> "지성"
            else -> "복합성"
        }
    }

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
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            // Text
            Text(
                text = "측 정 결 과",
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
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(android.graphics.Color.parseColor("#F7F1E5"))) // 원하는 배경 색상으로 설정
                    .padding(16.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (finalSkinType != null) {
                        Text("당신의 피부는 \"$finalSkinType\" 입니다.")
                    }
                    Row {
                        Column {
                            LoadImageFromUri(uri1)
                            Text(text = "$facePart1 : $skinType1")
                        }
                        Column {
                            LoadImageFromUri(uri2)
                            Text(text = "$facePart2 : $skinType2")
                        }
                        Column {
                            LoadImageFromUri(uri3)
                            Text(text = "$facePart3 : $skinType3")
                        }
                    }
                    HomeReturnButton2(
                        modifier = Modifier
                            .weight(1f),
                        navController,
                        auth,
                        uri1,
                        uri2,
                        uri3,
                        skinType1,
                        skinType2,
                        skinType3,
                        facePart1,
                        facePart2,
                        facePart3,
                        finalSkinType
                    )
                    AdPlaces()
                    AdPlaces()
                }

            }
        }
    }
}

fun uploadRawDataToFirestorage(
    imageUri: Uri,
    skinType: String,
    facePart: String?,
    onImageUploaded: (String) -> Unit
) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    // 이미지를 저장할 경로 및 이름 설정
    val imageRef = storageRef.child("raw/${facePart}/${skinType}/${UUID.randomUUID()}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                val imageUrl = downloadUri.toString()
                onImageUploaded(imageUrl)
            }
        }
        .addOnFailureListener {
            Log.e("실패", "망함")
            // 이미지 업로드 실패 시 처리
        }
}

@Composable
fun LoadImageFromUri(uri: Uri) {

    // Coil을 사용하여 이미지 로드
    Image(
        painter = rememberImagePainter(
            data = uri,
            builder = {
                crossfade(true) // crossfade 효과 사용
                transformations(CircleCropTransformation()) // 원형으로 자르기
            }
        ),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
    )
}