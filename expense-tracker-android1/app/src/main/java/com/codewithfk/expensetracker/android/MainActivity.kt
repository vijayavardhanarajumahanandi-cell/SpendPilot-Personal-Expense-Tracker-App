package com.codewithfk.expensetracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.codewithfk.expensetracker.android.ui.theme.ExpenseTrackerAndroidTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Firebase once when the app starts
        FirebaseApp.initializeApp(this)

        // ✅ Set up Compose UI
        setContent {
            ExpenseTrackerAndroidTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Launch your main navigation host
                    NavHostScreen()
                }
            }
        }
    }
}
