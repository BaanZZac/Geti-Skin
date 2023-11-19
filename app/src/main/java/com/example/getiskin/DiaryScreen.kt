package com.example.getiskin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Composable
fun DiaryScreen(auth: FirebaseAuth) {

    var skinAnalysisList by remember { mutableStateOf<List<SkinAnalysisData>>(emptyList()) }

    LaunchedEffect(Unit) {
        // 코루틴을 사용하여 데이터를 비동기적으로 가져옴
        skinAnalysisList = fetchDataFromFirestore(auth.currentUser?.uid ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // 이미지 리소스 ID로 변경
            contentDescription = null, // contentDescription은 필요에 따라 추가
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 이미지 높이 조절
        )

        Text(
            text = "나 의 일 지",
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


        // Journal Entries
        LazyColumn {
            items(skinAnalysisList) { entry ->
                JournalEntryCard(entry = entry)
            }
        }
    }
}

@Composable
fun JournalEntryCard(entry: SkinAnalysisData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color(0xFFE39368), shape = RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#F7F1E5")))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 날짜
                Text(
                    text = entry.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Divider(color = Color.Black, thickness = 1.dp)
                // 세로 구분선
                Spacer(modifier = Modifier.height(8.dp))

                // 내 피부상태
                Text(
                    text = "피부상태: ${entry.finalSkinType}",
                    style = MaterialTheme.typography.titleSmall
                )

                // 세로 구분선
                Spacer(modifier = Modifier.height(8.dp))

                // 사진
                LoadImageFromFirebase(entry.imageUrl1)
//                    Image(
//                        painter = painterResource(id = entry.imageuri1),
//                        contentDescription = "피부 사진",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .clip(MaterialTheme.shapes.medium)
//                    )
                Text(text = "${entry.facePart1} : ${entry.skinType1}")

                // 사진
                LoadImageFromFirebase(entry.imageUrl2)
                Text(text = "${entry.facePart2} : ${entry.skinType2}")

                // 사진
                LoadImageFromFirebase(entry.imageUrl3)
                Text(text = "${entry.facePart3} : ${entry.skinType3}")
            }
        }
    }
}

private suspend fun fetchDataFromFirestore(userId: String): List<SkinAnalysisData> = suspendCoroutine { continuation ->
    val db = FirebaseFirestore.getInstance()
    val result = mutableListOf<SkinAnalysisData>()

    // "skinAnalysis" 컬렉션에서 데이터 가져오기
    db.collection("skinAnalysis")
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val skinAnalysisData = document.toObject(SkinAnalysisData::class.java)
                if (skinAnalysisData.userID == userId) {
                    result.add(skinAnalysisData)
                }
            }
            continuation.resume(result)
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
            continuation.resumeWithException(exception)
        }
}

@Composable
fun LoadImageFromFirebase(imageUrl: String) {
    val painter = rememberImagePainter(data = imageUrl,
        builder = {
            crossfade(true)
        })

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}

//@Preview(showBackground = true)
//@Composable
//fun DiaryScreenPreview() {
//    val navController = rememberNavController()
//    MaterialTheme {
//        DiaryScreen(navController)
//    }
//}