package com.example.getiskin

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ShowToasts(message: String) {
    val context = LocalContext.current
    val toast = remember { Toast.makeText(context, message, Toast.LENGTH_SHORT) }
    toast.show()
}

@Composable
fun SkinButton(navController: NavController) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                navController.navigate("skin_analysis")
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.skin),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "피부 진단",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun DiaryButton(navController: NavController) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                navController.navigate("diary")
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.newdiary),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "나의 일지",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun ShopButton(navController: NavController) {

    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                navController.navigate("product")
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.cosmetics),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "상품 추천",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun ClinicButton(navController: NavController) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                navController.navigate("clinic")
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.clinics),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "피부관리샵 찾기",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}


@Composable
fun AdPlaces(adName: String, uriString: String) {
    var isClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                context.startActivity(intent)
            }
    ) {
        if (isClicked) {
            // 클릭되었을 때의 UI
            // 예를 들어, 광고 클릭 후에 할 작업을 여기에 추가
            ShowToasts("업체 웹페이지로 이동합니다")
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
            text = adName,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}

@Composable
fun HomeScreen(navController: NavController, onClicked: () -> Unit) {

    val oilyAd = "https://www.coupang.com/np/search?component=&q=%EC%A7%80%EC%84%B1+%ED%99%94%EC%9E%A5%ED%92%88&channel=user"
    val dryAd = "https://www.coupang.com/np/search?component=&q=%EA%B1%B4%EC%84%B1+%ED%99%94%EC%9E%A5%ED%92%88&channel=user"

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
                text = "H O M E",
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
                    SkinButton(
                        navController
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    DiaryButton(
                        navController
                    )

                    ShopButton(
                        navController
                    )
                    ClinicButton(
                        navController
                    )
                    LogoutButton(onClicked)
                    AdPlaces("지성 화장품 광고", oilyAd)
                    AdPlaces("건성 화장품 광고", dryAd)
                }
            }
        }
    }

}

@Composable
fun LogoutButton(onClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(10))
            .clickable {
                onClicked()
            }
    ) {
        // Image 등의 내용을 넣어줌
        Image(
            painter = painterResource(id = R.drawable.logout),
            contentDescription = null,
            contentScale = ContentScale.Crop, // 이미지가 잘릴 수 있도록 설정
            modifier = Modifier
                .fillMaxSize() // 이미지를 꽉 채우도록 설정
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
        )
        Text(
            text = "로그아웃",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent) // 텍스트 배경을 투명하게 설정
        )
    }
}
//@Preview
//@Composable
//fun HomeScreenPreview() {
//    // You can create a preview of the HomeScreen here
//    // For example, you can use a NavController with a LocalCompositionLocalProvider
//    // to simulate the navigation.
//    // Note: This is a simplified example; you may need to adjust it based on your actual navigation setup.
//    val navController = rememberNavController()
//    HomeScreen(navController = navController)
//}