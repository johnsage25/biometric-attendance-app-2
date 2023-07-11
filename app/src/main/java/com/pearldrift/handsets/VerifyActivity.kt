package com.pearldrift.handsets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme

class VerifyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {


            val scrollState = rememberScrollState()

            BASHandsetsCompatiblektTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight(1f)
                        .fillMaxWidth(1f)
                        .scrollable(
                            orientation = Orientation.Vertical,
                            interactionSource = null,
                            state = scrollState
                        ),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(topBar = {
                        TopBarWithBack(title = "Verify Account") {
                            finish()
                        }
                    }, bottomBar = {

                    },
                        content = ({
                            Column(modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()) {

                            }

                           Box(modifier = Modifier){
                             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                 Image(painter = painterResource(id = R.drawable.user_2_com),contentDescription = "", modifier = Modifier.size(height = 160.dp, width = 160.dp))

                             }
                           }
                        }))
                }
            }
        }
    }
}