package com.example.getiskin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Places.initialize(applicationContext, "AIzaSyBBi36Pj-bYMFFMQ9mAS-vwvOvusUqnglo")
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        // 구글 로그인 구현
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // default_web_client_id 에러 시 rebuild
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            val navController = rememberNavController()

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

            MaterialTheme {
                Surface {
                    // NavHost를 사용하여 네비게이션 구조를 설정합니다.
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("login") { LoginScreen(signInClicked = { launcher.launch(signInIntent) }) }
                        composable("home") { HomeScreen(navController, onClicked = { signOut(navController)}) }
                        composable("skin_analysis") { SkinAnalysisScreen(navController) }
                        composable("diary") { DiaryScreen(mAuth) }
                        composable("product") { ProductScreen() }
                        composable("clinic") { ClinicScreen(navController) }
                        composable("splash") { SplashContent(navController) }
                        composable("results/{headOil}/{noseOil}/{cheekOil}/{head}/{nose}/{cheek}/{headUri}/{noseUri}/{cheekUri}") {
                            val predictOily1 = it.arguments?.getString("headOil")?.toInt()
                            val predictOily2 = it.arguments?.getString("noseOil")?.toInt()
                            val predictOily3 = it.arguments?.getString("cheekOil")?.toInt()
                            val predictFace1 = it.arguments?.getString("head")?.toInt()
                            val predictFace2 = it.arguments?.getString("nose")?.toInt()
                            val predictFace3 = it.arguments?.getString("cheek")?.toInt()
                            val uri1 = it.arguments?.getString("headUri")
                            val uri2 = it.arguments?.getString("noseUri")
                            val uri3 = it.arguments?.getString("cheekUri")
                            ResultsScreen(
                                navController,
                                mAuth,
                                predictOily1,
                                predictOily2,
                                predictOily3,
                                predictFace1,
                                predictFace2,
                                predictFace3,
                                uri1,
                                uri2,
                                uri3
                            )
                        }
                        // 여기에 다른 화면들을 네비게이션 구조에 추가합니다.
                    }
                }
            }

        }
    }

    @Composable
    fun SplashContent(navController: NavController) {
        var scale by remember { mutableStateOf(1f) }
        var alpha by remember { mutableStateOf(1f) }
        val user: FirebaseUser? = mAuth.currentUser
        val startDestination = remember {
            if (user == null) {
                "login"
            } else {
                "home"
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            // 스플래시 화면에 표시할 내용 (이미지, 로고 등)
            Image(
                painter =
                painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = animateFloatAsState(targetValue = scale, animationSpec = tween(durationMillis = 1000)).value,
                        scaleY = animateFloatAsState(targetValue = scale, animationSpec = tween(durationMillis = 1000)).value,
                        alpha = alpha
                    )
            )
            // 스플래시 화면에서 HomeScreen으로 Navigation
            LaunchedEffect(true) {
                delay(1000) // 초기 딜레이
                scale = 1.5f
                alpha = 0f
                delay(1000) // 스케일 및 투명도 변경 후 딜레이
                navController.navigate(startDestination)
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