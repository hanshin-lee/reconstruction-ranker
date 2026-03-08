package com.ranker.reconstruction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ranker.reconstruction.data.UserComplexStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserComplexStore.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
