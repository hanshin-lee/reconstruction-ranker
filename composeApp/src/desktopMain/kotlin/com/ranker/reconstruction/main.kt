package com.ranker.reconstruction

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "재건축 랭커",
        state = rememberWindowState(width = 420.dp, height = 800.dp)
    ) {
        App()
    }
}
