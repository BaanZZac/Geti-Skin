package com.example.getiskin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getiskin.ui.theme.GetiSkinTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Places.initialize(applicationContext, "AIzaSyBBi36Pj-bYMFFMQ9mAS-vwvOvusUqnglo")
        val placesClient = Places.createClient(this)
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    MaterialTheme {
        Surface {
            // NavController 인스턴스를 생성합니다.
            val navController = rememberNavController()

            // NavHost를 사용하여 네비게이션 구조를 설정합니다.
            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("home") { HomeScreen(navController) }
                composable("skin_analysis") { SkinAnalysisScreen(navController) }
                composable("results") { ResultsScreen(navController) }
                composable("diary") { DiaryScreen(navController) }
                composable("shop") { ShopScreen(navController) }
                composable("clinic") { ClinicScreen(navController) }
                // 여기에 다른 화면들을 네비게이션 구조에 추가합니다.
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GetiSkinTheme {
//        Greeting("Android")
//    }
//}