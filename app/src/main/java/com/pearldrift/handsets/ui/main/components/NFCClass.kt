package com.pearldrift.handsets.ui.main.components

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.pearldrift.handsets.NfcActivity
import com.pearldrift.handsets.R

@Composable
fun NfcFunction(modifier: Modifier, changeState: MutableLiveData<String>, nfcParam: (nfcParam: String) -> Unit){

    var context = LocalContext.current
    var cardData by remember() {
        mutableStateOf("")
    }


    changeState.observeForever{
        if(it.toBoolean()){
            cardData =""
        }
    }


    var nfcActivity = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                var cardstr = it.data?.getStringExtra("cardstr")
                cardData = cardstr.toString()
                nfcParam(cardstr.toString())
            }
        }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = {
                nfcActivity.launch(
                    Intent(
                        context,
                        NfcActivity::class.java
                    )
                )
            }, modifier = Modifier
                .padding(vertical = 10.dp)
                .height(200.dp).width(200.dp)
                .clickable(
                    onClick = { /* Ignoring onClick */ },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true
                    )
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(1f)
            ) {

                if (cardData.isNotEmpty()) {


                    Image(
                        painter = painterResource(
                            id = R.drawable.ic_nfc_card_success
                        ),
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f),
                        contentDescription = "nfc"
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(
                                horizontal = 25.dp,
                                vertical = 10.dp
                            )
                            .size(
                                width = 200.dp,
                                height = 40.dp
                            )
                            .fillMaxWidth(1f)
                            .background(Color.White)
                    ) {
                        Text(
                            text = "Completed",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.Black
                            ),
                        )
                    }

                } else {
                    Image(
                        painter = painterResource(
                            id = R.drawable.ic_nfc_card
                        ),
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f),
                        contentDescription = "nfc"
                    )
                }

            }
        }
    }


}