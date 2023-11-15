package com.example.getiskin

import android.hardware.display.DeviceProductInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProductScreen(navController: NavController) {
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopAppBar(
//            title = {
//                Text(
//                    text = "지성",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentSize(Alignment.Center)
//                )
//            }
//        )
//        Divider(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(10.dp) // 높이 조절
//                .padding(vertical = 8.dp), // 상하 여백 조절
//            color = Color.Black
//        )
//
//
//        ProductCard(
//            imageResourceId = R.drawable.ic_launcher_background,
//            manufacturer = "라운드랩",
//            name = "1025 독도 토너",
//            price = "19,900원",
//            additionalInfo = "300ml"
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        ProductCard(
//            imageResourceId = R.drawable.ic_launcher_background,
//            manufacturer = "비플레인",
//            name = "녹두 약산성 클렌징폼",
//            price = "18,900원",
//            additionalInfo = "120ml"
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        ProductCard(
//            imageResourceId = R.drawable.ic_launcher_background,
//            manufacturer = "이즈앤트리",
//            name = "초저분자 히아루론산 토너",
//            price = "14,900원",
//            additionalInfo = "300ml"
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        ProductCard(
//            imageResourceId = R.drawable.ic_launcher_background,
//            manufacturer = "이즈앤트리",
//            name = "초저분자 히아루론산 토너",
//            price = "14,900원",
//            additionalInfo = "300ml"
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//
//        ProductCard(
//            imageResourceId = R.drawable.ic_launcher_background,
//            manufacturer = "라운드랩",
//            name = "자작나무 수분 선크림",
//            price = "19,900원",
//            additionalInfo = "80ml"
//        )
//    }
//}
//
//
//@Composable
//fun ProductCard(imageResourceId: Int, manufacturer: String, name: String, price: String,additionalInfo: String) {
//    Card(
//        modifier = Modifier
//            .width(500.dp)
//            .clickable { }
//            .background(Color.White),
//        shape = MaterialTheme.shapes.medium,
//    ) {
//        Row(modifier = Modifier.padding(8.dp)) {
//            Image(
//                painter = painterResource(id = imageResourceId),
//                contentDescription = "image",
//                modifier = Modifier
//                    .size(110.dp)
//                    .fillMaxHeight()
//            )
//            Column(modifier = Modifier.padding(8.dp)) {
//                Text(
//                    text = manufacturer,
//                    color = Color.Gray,
//                    fontWeight = FontWeight.Normal,
//                    modifier = Modifier.align(Alignment.Start)
//                )
//                Text(
//                    text = name,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//                Text(
//                    text = price,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = additionalInfo,
//                    color = Color.Gray, // 추가 정보의 색상을 지정
//                    fontWeight = FontWeight.Normal
//                )
//            }
//        }
//    }
//}

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
fun ProductScreen(navController: NavController, skinType: String = "Oily") {
    // 현재 선택된 피부 유형을 추적하는 변수
    var selectedSkinType by remember { mutableStateOf("Oily") }
    // 피부 유형에 따른 상품 리스트
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }

    DisposableEffect(selectedSkinType) {
        productList = generateProductList(selectedSkinType)

        onDispose { /* Clean up if needed */ }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = selectedSkinType, // 선택된 피부 유형에 따라 타이틀 변경
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
            }
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp) // 높이 조절
                .padding(vertical = 8.dp), // 상하 여백 조절
            color = Color.Black
        )

        // 피부 유형을 선택하는 버튼 행
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 각 피부 유형 버튼
            SkinTypeButton("Oily", selectedSkinType, onSelectSkinType = { selectedSkinType = it })
            SkinTypeButton("Dry", selectedSkinType, onSelectSkinType = { selectedSkinType = it })
            SkinTypeButton("Combination", selectedSkinType, onSelectSkinType = { selectedSkinType = it })
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
        colors = ButtonDefaults.buttonColors(
            // 선택된 피부 유형에 따라 버튼 배경색 변경
            containerColor = if (skinType == selectedSkinType) Color.Gray else Color.Black
        )
    ) {
        Text(text = skinType)
    }
}


@Composable
fun ProductCard(imageResourceId: Int, manufacturer: String, name: String, price: String, additionalInfo: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .background(Color.White),
        shape = MaterialTheme.shapes.medium,
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


fun generateProductList(skinType: String): List<Product> {
    // 각 피부 유형에 따른 상품 리스트를 생성하는 로직
    // (이 부분은 실제 서버에서 데이터를 가져오는 로직 등으로 변경할 수 있음)
    return when (skinType) {
        "Oily" -> listOf(
            Product(R.drawable.ic_launcher_background, "라운드랩", "1025 독도 토너", "19,900원", "300ml"),
            Product(R.drawable.ic_launcher_background, "비플레인", "녹두 약산성 클렌징폼", "18,900원", "120ml"),
            // ...
        )
        "Dry" -> listOf(
            Product(R.drawable.ic_launcher_background, "이즈앤트리", "초저분자 히아루론산 토너", "14,900원", "300ml"),
            // ...
        )
        "Combination" -> listOf(
            Product(R.drawable.ic_launcher_background, "라운드랩", "자작나무 수분 선크림", "19,900원", "80ml"),
            // ...
        )
        else -> emptyList()
    }
}





//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    val navController = rememberNavController()
//    MaterialTheme {
//        ProductScreen(navController)
//    }
//}

