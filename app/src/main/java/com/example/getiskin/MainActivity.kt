package com.example.getiskin

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Places.initialize(applicationContext, "AIzaSyBBi36Pj-bYMFFMQ9mAS-vwvOvusUqnglo")
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }



    }
    data class PredictResponse(
        @SerializedName("predicted_class")
        val predictedClass: Int
    )


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