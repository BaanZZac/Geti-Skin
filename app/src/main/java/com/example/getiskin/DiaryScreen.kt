package com.example.getiskin

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen2(navController: NavController, auth: FirebaseAuth) {
    // TODO: DB에서 날짜, 피부상태, 사진 등의 데이터 가져오기
    val db = Firebase.firestore
    val user = auth.currentUser
    val uid = user?.uid ?: ""

    val journalEntries by remember { mutableStateOf(emptyList<JournalEntry>()) }

    LaunchedEffect(uid) {
        val document = db.collection("records").document(uid).get().await()
        if (document.exists()) {
            val data = document.data ?: emptyMap()
            // TODO: Firestore에서 필요한 데이터를 가져와서 journalEntries에 추가
            // 예시: val date = data["date"] as String
            //      val skinCondition = data["skinCondition"] as String
            //      val photoResId = R.drawable.ic_launcher_background // 임시값, 실제로는 저장된 이미지의 리소스 ID 사용
            //      journalEntries = listOf(JournalEntry(date, skinCondition, photoResId))
        }
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
            items(journalEntries) { entry ->
                JournalEntryCard(entry = entry)
            }
        }
    }
}

@Composable
fun JournalEntryCard(entry: JournalEntry) {
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
                    text = entry.time,
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
                    text = "피부상태: ${entry.finalPredicts}",
                    style = MaterialTheme.typography.titleSmall
                )

                // 세로 구분선
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    // 사진
//                    Image(
//                        painter = painterResource(id = entry.imageuri1),
//                        contentDescription = "피부 사진",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .clip(MaterialTheme.shapes.medium)
//                    )
                    Text(text = "${entry.skinType1} : ${entry.facePart1}")

                    // 사진
//                    Image(
//                        painter = painterResource(id = entry.imageuri2),
//                        contentDescription = "피부 사진",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .clip(MaterialTheme.shapes.medium)
//                    )
                    Text(text = "${entry.skinType2} : ${entry.facePart2}")

                    // 사진
//                    Image(
//                        painter = painterResource(id = entry.imageuri3),
//                        contentDescription = "피부 사진",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .clip(MaterialTheme.shapes.medium)
//                    )
                    Text(text = "${entry.skinType3} : ${entry.facePart3}")
                }

            }
        }
    }
}

data class JournalEntry(
    val time: String,
    val finalPredicts: String,
    val skinType1: String,
    val skinType2: String,
    val skinType3: String,
    val facePart1: String,
    val facePart2: String,
    val facePart3: String,
    val imageUri1: Uri,
    val imageUri2: Uri,
    val imageUri3: Uri
)
@Composable
fun DiaryScreen(navController: NavController, auth: FirebaseAuth) {
    var skinAnalysisList by remember { mutableStateOf<List<SkinAnalysisData>>(emptyList()) }

    LaunchedEffect(Unit) {
        // 코루틴을 사용하여 데이터를 비동기적으로 가져옴
        skinAnalysisList = fetchDataFromFirestore()
    }

    Column {
        Text(text = "왜 안나옴?")
        SkinAnalysisList(skinAnalysisList)
    }
}

private suspend fun fetchDataFromFirestore(): List<SkinAnalysisData> = suspendCoroutine { continuation ->
    val db = FirebaseFirestore.getInstance()
    val result = mutableListOf<SkinAnalysisData>()

    // "skinAnalysis" 컬렉션에서 데이터 가져오기
    db.collection("skinAnalysis")
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val skinAnalysisData = document.toObject(SkinAnalysisData::class.java)
                result.add(skinAnalysisData)
            }
            continuation.resume(result)
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
            continuation.resumeWithException(exception)
        }
}

@Composable
private fun SkinAnalysisList(skinAnalysisList: List<SkinAnalysisData>) {
    LazyColumn {
        items(skinAnalysisList) { skinAnalysisData ->
            SkinAnalysisItem(skinAnalysisData)
        }
    }
}

@Composable
private fun SkinAnalysisItem(skinAnalysisData: SkinAnalysisData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Gray)
            .padding(16.dp)
    ) {
//        Text("UserID: ${skinAnalysisData.userID}")
//        Spacer(modifier = Modifier.height(8.dp))
        Text("Timestamp: ${skinAnalysisData.timestamp}")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Your Skin Type: ${skinAnalysisData.finalSkinType}")
        Spacer(modifier = Modifier.height(8.dp))
        LoadImageFromFirebase(imageUrl = skinAnalysisData.imageUrl1)
        Text(text = "${skinAnalysisData.imageUrl1}")
        Text("First : ${skinAnalysisData.facePart1} : ${skinAnalysisData.skinType1}")
        LoadImageFromFirebase(imageUrl = skinAnalysisData.imageUrl2)
        Text("Second : ${skinAnalysisData.facePart2} : ${skinAnalysisData.skinType2}")
        LoadImageFromFirebase(imageUrl = skinAnalysisData.imageUrl3)
        Text("Third : ${skinAnalysisData.facePart3} : ${skinAnalysisData.skinType3}")
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
        contentDescription = null, // Content description can be null for decorative images
        modifier = Modifier.size(30.dp)
    )
}

//@Composable
//fun DiaryScreen(navController: NavController, auth: FirebaseAuth) {
//    var skinAnalysisList: MutableList<SkinAnalysisData> = mutableListOf()
//    LaunchedEffect(Unit) {
//        skinAnalysisList = fetchDataFromFirestore()
//    }
//    Column {
//        Text(text = "왜 안나옴?")
//        SkinAnalysisList(skinAnalysisList)
//    }
//}
//
//private fun fetchDataFromFirestore(): MutableList<SkinAnalysisData> {
//
//    val db = FirebaseFirestore.getInstance()
//    val result = mutableListOf<SkinAnalysisData>()
//
//    // "skinAnalysis" 컬렉션에서 데이터 가져오기
//    db.collection("skinAnalysis")
//        .get()
//        .addOnSuccessListener { querySnapshot ->
//            for (document in querySnapshot) {
//                val skinAnalysisData = document.toObject(SkinAnalysisData::class.java)
//                result.add(skinAnalysisData)
//            }
//        }
//        .addOnFailureListener { exception ->
//            println("Error getting documents: $exception")
//        }
//
//    return result
//}
//
//@Composable
//private fun SkinAnalysisList(skinAnalysisList: List<SkinAnalysisData>) {
//    LazyColumn {
//        items(skinAnalysisList) { skinAnalysisData ->
//            SkinAnalysisItem(skinAnalysisData)
//        }
//    }
//}
//
//@Composable
//private fun SkinAnalysisItem(skinAnalysisData: SkinAnalysisData) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .background(Color.Gray)
//            .padding(16.dp)
//    ) {
//        Text("UserID: ${skinAnalysisData.userID}")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Timestamp: ${skinAnalysisData.timestamp}")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Skin Type: ${skinAnalysisData.skinType}")
//        Spacer(modifier = Modifier.height(8.dp))
//        Text("Face Part: ${skinAnalysisData.facePart}")
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun DiaryScreenPreview() {
//    val navController = rememberNavController()
//    MaterialTheme {
//        DiaryScreen(navController)
//    }
//}