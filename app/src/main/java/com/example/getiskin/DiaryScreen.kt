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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(navController: NavController) {
    // TODO: DB에서 날짜, 피부상태, 사진 등의 데이터 가져오기
    val journalEntries = listOf(
        JournalEntry("2023-11-14", "좋음", R.drawable.ic_launcher_background),
        JournalEntry("2023-11-13", "보통", R.drawable.ic_launcher_foreground),
        JournalEntry("2023-11-13", "보통", R.drawable.ic_launcher_foreground),
        JournalEntry("2023-11-13", "보통", R.drawable.ic_launcher_foreground)
        // 추가적인 항목은 필요에 따라 수정
    )

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
                    text = entry.date,
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
                    text = "피부상태: ${entry.skinCondition}",
                    style = MaterialTheme.typography.titleSmall
                )

                // 세로 구분선
                Spacer(modifier = Modifier.height(8.dp))

                // 사진
                Image(
                    painter = painterResource(id = entry.photoResId),
                    contentDescription = "피부 사진",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

data class JournalEntry(val date: String, val skinCondition: String, val photoResId: Int)


@Preview(showBackground = true)
@Composable
fun DiaryScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        DiaryScreen(navController)
    }
}