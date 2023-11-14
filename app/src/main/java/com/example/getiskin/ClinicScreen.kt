package com.example.getiskin


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest


@Composable
fun ClinicScreen(navController: NavController) {

    val searchText = "피부관리"
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val context = LocalContext.current
    var isLocationPermissionGranted by remember { mutableStateOf(false) }

    // 위치 권한 요청을 위한 런처
    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isLocationPermissionGranted = isGranted
        }

    // 위치 권한 요청 버튼
    Button(
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 이미 권한이 있는 경우
                isLocationPermissionGranted = true
            } else {
                // 권한이 없는 경우
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text("Request Location Permission")
    }

    // 위치 권한 체크 및 요청
    if (isLocationPermissionGranted) {
        // 위치 권한이 허용된 경우 위치 정보 가져오기 시도
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // 위치 정보 가져오기 성공
                val currentLatLng = LatLng(location.latitude, location.longitude)

                // 검색어와 현재 위치 기반으로 Google Maps 열기
                openGoogleMaps(context, currentLatLng, searchText)
            } else {
                // 위치 정보 없음
            }
        }.addOnFailureListener { exception ->
            // 위치 정보 가져오기 실패
        }
    } else {
        // 위치 권한이 거부된 경우 처리
        // 여기에 권한이 거부되었을 때의 동작을 추가할 수 있습니다.
    }
}


// Google Maps 열기 함수
private fun openGoogleMaps(context: Context, destinationLatLng: LatLng, searchText: String) {
    val mapIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:${destinationLatLng.latitude},${destinationLatLng.longitude}?q=$searchText")
    )

    // Google Maps 앱이 설치되어 있는 경우 Google Maps 앱에서 지도를 엽니다.
    // 설치되어 있지 않은 경우 웹 브라우저에서 지도를 엽니다.
    mapIntent.setPackage("com.google.android.apps.maps")
    context.startActivity(mapIntent)
}

