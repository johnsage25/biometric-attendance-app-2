package com.pearldrift.handsets

import android.R
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.datepicker.CompositeDateValidator.allOf
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme



class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}

@Composable
fun Greeting2(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    BASHandsetsCompatiblektTheme {
        Greeting2("Android")
    }
}