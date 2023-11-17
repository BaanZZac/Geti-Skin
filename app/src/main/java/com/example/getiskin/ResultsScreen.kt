package com.example.getiskin

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ResultsScreen(navController: NavController, predict: Int?, predict2: Int?) {
    Column {
        Text(text = "$predict")
        Text(text = "$predict2")
    }
}