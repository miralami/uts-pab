package com.example.questlog

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.questlog.ui.QuestLogApp
import com.example.questlog.ui.theme.QuestLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // App selalu bertema terang, jadi paksa ikon system bar berwarna gelap.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            // API < 26 belum mendukung ikon navigasi gelap.
            navigationBarStyle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            } else {
                SystemBarStyle.dark(Color.BLACK)
            }
        )
        setContent {
            QuestLogTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuestLogApp()
                }
            }
        }
    }
}
