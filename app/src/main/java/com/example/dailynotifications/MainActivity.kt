package com.example.dailynotifications


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationStart()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationStart(){
    RemindCreationScreen()
}