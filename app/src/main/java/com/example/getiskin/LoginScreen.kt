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
fun LoginScreen(navController: NavController) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    val auth = FirebaseAuth.getInstance()
//    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(context.getString(R.string.default_web_client_id)) // `strings.xml`에 정의된 ID 사용
//        .requestEmail()
//        .build()
//    val googleSignInClient = GoogleSignIn.getClient(context, gso)
//
//    val googleSignInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            coroutineScope.launch {
//                try {
//                    val account = task.getResult(ApiException::class.java)
//                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//                    auth.signInWithCredential(credential).addOnCompleteListener { authResult ->
//                        if (authResult.isSuccessful) {
//                            // 로그인 성공
//                            navController.navigate("home")
//                        } else {
//                            // 로그인 실패 처리
//                            Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } catch (e: ApiException) {
//                    // Google 로그인 실패 처리
//                    Toast.makeText(context, "Google 로그인 실패: ${e.statusCode}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    Button(onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }) {
//        Text("구글 계정으로 로그인")
//    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... 여기에 로그인 화면의 다른 UI 요소를 추가할 수 있습니다 ...

        Button(
            onClick = {
                // 버튼 클릭 시 "home" 라우트로 네비게이션
                navController.navigate("home")
            }
        ) {
            Text(text = "로그인")
        }
    }
}
