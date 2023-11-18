package com.example.getiskin

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ResultsScreen(
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