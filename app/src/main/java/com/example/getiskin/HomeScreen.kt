package com.example.getiskin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... 여기에 홈 화면의 다른 UI 요소를 추가할 수 있습니다 ...

        Button(
            onClick = {
                // '피부 진단' 버튼 클릭 시 "skin_analysis" 라우트로 네비게이션
                navController.navigate("skin_analysis")
            }
        ) {
            Text(text = "피부 진단")
        }
        Button(onClick = { navController.navigate("diary") }) {
            Text("일지 보기")
        }
    }
}

