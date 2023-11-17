package com.example.getiskin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Places.initialize(applicationContext, "AIzaSyBBi36Pj-bYMFFMQ9mAS-vwvOvusUqnglo")
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        // 구글 로그인 구현
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // default_web_client_id 에러 시 rebuild
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            val user: FirebaseUser? = mAuth.currentUser
            val navController = rememberNavController()


            val startDestination = remember {
                if (user == null) {
                    "login"
                } else {
                    "home"
                }
            }
            val signInIntent = googleSignInClient.signInIntent
            val launcher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                    val data = result.data
                    // result returned from launching the intent from GoogleSignInApi.getSignInIntent()
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val exception = task.exception
                    if (task.isSuccessful) {
                        try {
                            // Google SignIn was successful, authenticate with firebase
                            val account = task.getResult(ApiException::class.java)!!
                            firebaseAuthWithGoogle(account.idToken!!)
                            navController.popBackStack()
                            navController.navigate("home")
                        } catch (e: Exception) {
                            // Google SignIn failed
                            Log.d("SignIn", "로그인 실패")
                        }
                    } else {
                        Log.d("SignIn", exception.toString())
                    }


                }

//            MyApp(launcher, signInIntent)

            MaterialTheme {
                Surface {
                    // NavController 인스턴스를 생성합니다.
//                    val navController = rememberNavController()

                    // NavHost를 사용하여 네비게이션 구조를 설정합니다.
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(signInClicked = {
                                launcher.launch(signInIntent)
                            })
                        }
                        composable("home") { HomeScreen(navController) }
                        composable("skin_analysis") { SkinAnalysisScreen(navController) }
                        composable("results/{predict1}/{predict2}") {
                            val predictOily = it.arguments?.getString("predict1")?.toInt()
                            val predictFace = it.arguments?.getString("predict2")?.toInt()
                            ResultsScreen(navController, predictOily, predictFace)
                        }
                        composable("diary") { DiaryScreen(navController) }
                        composable("product") { ProductScreen(navController) }
                        composable("logout"){
                            Logout(onClicked = { signOut(navController) })
                        }
                        composable("shop") { ShopScreen(navController) }
                        composable("clinic") { ClinicScreen(navController) }
                        // 여기에 다른 화면들을 네비게이션 구조에 추가합니다.
                    }
                }
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // SignIn Successful
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    // SignIn Failed
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut(navController: NavController) {
        // get the google account
        val googleSignInClient: GoogleSignInClient

        // configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Sign Out of all accounts
        mAuth.signOut()
        googleSignInClient.signOut().addOnSuccessListener {
            Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
            navController.navigate("login")
        }.addOnFailureListener {
            Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_SHORT).show()
        }
    }
}

//@Composable
//fun MyApp(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, signInIntent: Intent) {
//    MaterialTheme {
//        Surface {
//            // NavController 인스턴스를 생성합니다.
//            val navController = rememberNavController()
//
//            // NavHost를 사용하여 네비게이션 구조를 설정합니다.
//            NavHost(navController = navController, startDestination = "login") {
//                composable("login") {
//                    LoginScreen(signInClicked = {
//                        launcher.launch(signInIntent)
//                    })
//                }
//                composable("home") { HomeScreen(navController) }
//                composable("skin_analysis") { SkinAnalysisScreen(navController) }
//                composable("results/{predict1}/{predict2}") {
//                    val predictOily = it.arguments?.getString("predict1")?.toInt()
//                    val predictFace = it.arguments?.getString("predict2")?.toInt()
//                    ResultsScreen(navController, predictOily, predictFace)
//                }
//                composable("diary") { DiaryScreen(navController) }
//                composable("product") { ProductScreen(navController) }
//                composable("logout"){
//                    Logout(onClicked = { signOut(navController) })
//                }
//                composable("shop") { ShopScreen(navController) }
//                composable("clinic") { ClinicScreen(navController) }
//                // 여기에 다른 화면들을 네비게이션 구조에 추가합니다.
//            }
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GetiSkinTheme {
//        Greeting("Android")
//    }
//}