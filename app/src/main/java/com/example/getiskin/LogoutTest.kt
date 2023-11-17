package com.example.getiskin


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Logout(onClicked: () -> Unit){
    Box(modifier = Modifier
        .clickable { onClicked() }
        .background(color = Color(0xff75d1ff), shape = RoundedCornerShape(8.dp))
        .size(width = 100.dp, height = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "로그아웃", color = Color.White, fontWeight = FontWeight.Bold)
    }
}