package com.example.getiskin

import android.hardware.display.DeviceProductInfo
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


// Product 데이터 클래스 정의
data class Product(
    val imageResourceId: Int,
    val manufacturer: String,
    val name: String,
    val price: String,
    val additionalInfo: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController, skinType: String = "지성") {
    // 현재 선택된 피부 유형을 추적하는 변수
    var selectedSkinType by remember { mutableStateOf("지성") }
    // 피부 유형에 따른 상품 리스트
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }

    DisposableEffect(selectedSkinType) {
        productList = generateProductList(selectedSkinType)

        onDispose { /* Clean up if needed */ }
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

        // Text
        Text(
            text = "제 품 추 천",
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
        Spacer(modifier = Modifier.height(8.dp))
        TopAppBar(
            title = {
                Text(
                    text = selectedSkinType, // 선택된 피부 유형에 따라 타이틀 변경
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp, // 원하는 크기로 조절
                    fontWeight = FontWeight.Bold,
                    color = Color(android.graphics.Color.parseColor("#e39368"))
                )
            },
            modifier = Modifier
                .height(56.dp)
                .border(2.dp, Color(0xFFE39368), shape = RoundedCornerShape(16.dp))
        )

//        Divider(color = Color.Black, thickness = 1.dp)

        // 피부 유형을 선택하는 버튼 행
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 각 피부 유형 버튼
            SkinTypeButton("지성", selectedSkinType, onSelectSkinType = { selectedSkinType = it })
            SkinTypeButton("건성", selectedSkinType, onSelectSkinType = { selectedSkinType = it })
            SkinTypeButton("복합성", selectedSkinType, onSelectSkinType = { selectedSkinType = it })
        }

        // 상품 리스트를 보여주는 LazyColumn
        LazyColumn {
            items(productList) { product ->
                ProductCard(
                    imageResourceId = product.imageResourceId,
                    manufacturer = product.manufacturer,
                    name = product.name,
                    price = product.price,
                    additionalInfo = product.additionalInfo
                )
            }
        }
    }
}


// 피부 유형을 선택하는 버튼 Composable
@Composable
fun SkinTypeButton(skinType: String, selectedSkinType: String, onSelectSkinType: (String) -> Unit) {
    // 피부 유형을 나타내는 버튼
    Button(
        onClick = { onSelectSkinType(skinType) },
        colors = buttonColors(
            containerColor = if (skinType == selectedSkinType) Color.Black else Color(0xFFE39368)
        )
    ) {
        Text(text = skinType, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun ProductCard(
    imageResourceId: Int,
    manufacturer: String,
    name: String,
    price: String,
    additionalInfo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color(0xFFE39368), shape = RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = "image",
                    modifier = Modifier
                        .size(110.dp)
                        .fillMaxHeight()
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = manufacturer,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = price,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = additionalInfo,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}


fun generateProductList(skinType: String): List<Product> {
    // 각 피부 유형에 따른 상품 리스트를 생성하는 로직
    // (이 부분은 실제 서버에서 데이터를 가져오는 로직 등으로 변경할 수 있음)
    return when (skinType) {
        "지성" -> listOf(
            Product(R.drawable.dokdo, "라운드랩", "1025 독도 토너", "19,900원", "300ml"),
            Product(R.drawable.beplain, "비플레인", "녹두 약산성 클렌징폼", "18,900원", "120ml"),
            Product(R.drawable.blemish, "닥터지", "레드 블레미쉬 클리어 수딩 크림", "26,600원", "50ml"),
            Product(R.drawable.jajak, "라운드랩", "자작나무 수분 선크림", "19,900원", "80ml"),
            Product(R.drawable.power, "잇츠스킨", "파워 10 감초줄렌 젤리패드", "27,500원", "120ml"),
            Product(R.drawable.oil, "마녀공장", "퓨어 클렌징 오일", "24,500원", "400ml"),
            // ...
        )

        "건성" -> listOf(
            Product(R.drawable.toner, "이즈앤트리", "초저분자 히아루론산 토너", "14,900원", "300ml"),
            Product(R.drawable.dalba_w, "달바", "화이트 트러플 더블 세럼 앤 크림", "78,000원", "70g"),
            Product(R.drawable.dalba_s, "달바", "워터풀 선크림", "34,000원", "50ml"),
            Product(R.drawable.snature, "에스네이처", "아쿠아 스쿠알란 수분크림", "29,900원", "160ml"),
            Product(R.drawable.seramaid, "아이레시피", "세라마이드 유자 힐링 클렌징 밤", "48,000원", "120g"),
            Product(R.drawable.carrot, "스킨푸드", "캐롯 카로틴 카밍워터 패드", "20,800원", "260g"),
        )

        "복합성" -> listOf(
            Product(R.drawable.aqua, "에스네이처", "아쿠아 오아시스 토너", "19,900원", "300ml"),
            Product(R.drawable.madagascar, "스킨1004", "마다가스카르 센텔라 히알루-시카 워터핏 선세럼", "14,000원", "50ml"),
            Product(R.drawable.beplain, "비플레인", "녹두 약산성 클렌징폼", "18,900원", "120ml"),
            Product(R.drawable.dokdo, "라운드랩", "1025 독도 토너", "19,900원", "300ml"),
            Product(R.drawable.torriden, "토리든", "다이브인 저분자 히알루론산 수딩 크림", "14,500원", "100ml"),
            Product(R.drawable.atoberrier, "에스트라", "아토베리어365크림", "23,250원", "80ml"),
            // ...
        )

        else -> emptyList()
    }
}


@Preview(showBackground = true)
@Composable
fun ProductScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        ProductScreen(navController)
    }
}

