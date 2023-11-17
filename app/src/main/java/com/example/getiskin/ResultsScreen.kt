package com.example.getiskin

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ResultsScreen(navController: NavController, oilyPredict: Int?, facePredict: Int?, uri: String?) {
    Column {
        val context = LocalContext.current
        //버전이 낮은건 else를 실행한다
        //ImageDecoder쪽을 복사하고 else todo랑 같이 검색하면됨
        val imageUri = Uri.parse(uri)
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val decodeBitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    imageUri
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
        Text(text = "당신의 피부는 \"지성 or 건성 or 복합성\" 입니다!")
        Text(text = "이마 : 지성 or 건성")
        Text(text = "코 : 지성 or 건성")
        Text(text = "볼 : 지성 or 건성")
        Text(text = "$oilyPredict")
        Text(text = "$facePredict")
        Button(onClick = {
        /*TODO : predict값을 userID, timestemp, fireStorage uri 값과 함께 FireStore에 전달, 이후 Daily에서 받아와 사용*/
        }) {
            Text(text = "진단 결과 저장하기")
        }
    }
}