package com.example.getiskin

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//@SuppressLint("StaticFieldLeak")
//val db = Firebase.firestore
//private lateinit var auth: FirebaseAuth
//@SuppressLint("StaticFieldLeak")
//private lateinit var firestore: FirebaseFirestore

@Composable
fun HomeReturnButton2(modifier: Modifier, navController: NavController, auth: FirebaseAuth) {
    val db = Firebase.firestore
    val user = auth.currentUser
    val uid = user?.uid ?: "" //유저
    val currentTime = System.currentTimeMillis() //버튼을 눌렀을때 날짜
    val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime)
    val predicts = mutableListOf<IntArray>() //결과값
    var imageUri: Uri? = null

    val analysisData = hashMapOf(
        "Time" to FieldValue.arrayUnion(time),
        "predicts" to predicts
    )

    // 현재 시간을 가져오는 함수
    fun getCurrentTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {

                // 이미지 업로드
                if (imageUri != null) {
                    uploadImageToFirestore(imageUri) { imageUrl ->
                        // 이미지 업로드가 성공한 경우에 Firestore에 데이터 저장
                        db
                            .collection("records")
                            .document(uid)
                            .update(
                                "Time", FieldValue.arrayUnion(getCurrentTime()),
                                "predicts", predicts,
                                "imageUrl", imageUrl
                            )
                            .addOnSuccessListener {
                                // 저장 성공 시 추가 작업 수행
                                navController.navigate("home")
                            }
                    }
                } else {
                    // 이미지가 없는 경우에는 Firestore에 데이터 저장만 수행
                    db
                        .collection("records")
                        .document(uid)
                        .update(
                            "Time", FieldValue.arrayUnion(getCurrentTime()),
                            "predicts", predicts
                        )
                        .addOnSuccessListener {
                            // 저장 성공 시 추가 작업 수행
                            navController.navigate("home")
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

fun uploadImageToFirestore(imageUri: Uri, onImageUploaded: (String) -> Unit) {
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
            showToast(message = "광고가 나올 화면입니다.")
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
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun ResultsScreen(navController: NavController, predict: Int?, predict2: Int?, auth: FirebaseAuth) {

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
                    Text(text = "$predict")
                    Text(text = "$predict2")
                    HomeReturnButton2(
                        modifier = Modifier
                            .weight(1f), navController, auth
                    )
                    AdPlaces()
                    AdPlaces()
                }

            }
        }
    }
}

@Preview
@Composable
fun ResultScreenPreview() {
    // You can create a preview of the HomeScreen here
    // For example, you can use a NavController with a LocalCompositionLocalProvider
    // to simulate the navigation.
    // Note: This is a simplified example; you may need to adjust it based on your actual navigation setup.
    val navController = rememberNavController()
    ResultsScreen(navController = navController, 100, 100, auth = FirebaseAuth.getInstance())
}

@Composable
fun ResultsScreen2(
    navController: NavController,
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
    Column {
        val context = LocalContext.current
        //버전이 낮은건 else를 실행한다
        //ImageDecoder쪽을 복사하고 else todo랑 같이 검색하면됨
        val headUri = Uri.parse(headUriString)
        val noseUri = Uri.parse(noseUriString)
        val cheekUri = Uri.parse(cheekUriString)
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

        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            val firstBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val decodeBitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver, headUri
                    )
                )
                decodeBitmap
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, headUri)
            }
            Image(
                bitmap = firstBitmap.asImageBitmap(), contentDescription = "", modifier = Modifier
                    .size(150.dp)
                    .shadow(2.dp)
            )
            Text(text = "$facePart1 : $skinType1")

            val secondBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val decodeBitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver, noseUri
                    )
                )
                decodeBitmap
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, noseUri)
            }
            Image(
                bitmap = secondBitmap.asImageBitmap(), contentDescription = "", modifier = Modifier
                    .size(130.dp)
                    .shadow(2.dp)
            )
            Text(text = "$facePart2 : $skinType2")

            val thirdBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val decodeBitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver, cheekUri
                    )
                )
                decodeBitmap
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, cheekUri)
            }
            Image(
                bitmap = thirdBitmap.asImageBitmap(), contentDescription = "", modifier = Modifier
                    .size(130.dp)
                    .shadow(2.dp)
            )
            Text(text = "$facePart3 : $skinType3")

        }
        if (predictOilHead != null && predictOilNose != null && predictOilCheek != null) {
            if (predictOilHead != predictOilNose || predictOilNose != predictOilCheek || predictOilCheek != predictOilHead) {
                Text("당신의 피부는 \"복합성\" 입니다.")
            } else if (predictOilHead > 3 && predictOilNose > 3 && predictOilCheek > 3) {
                Text("당신의 피부는 \"건성\" 입니다.")
            } else if (predictOilHead <= 3 && predictOilNose <= 3 && predictOilCheek <= 3) {
                Text("당신의 피부는 \"지성\" 입니다.")
            }
        }
        Button(onClick = {
            /*TODO : predict값을 userID, timestemp, fireStorage uri 값과 함께 FireStore에 전달, 이후 Daily에서 받아와 사용*/
        }) {
            Text(text = "진단 결과 저장하기")
        }
    }
}