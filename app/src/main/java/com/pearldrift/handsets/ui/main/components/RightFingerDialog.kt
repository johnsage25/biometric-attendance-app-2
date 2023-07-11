package com.pearldrift.handsets.ui.main.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.compose.*
import com.fgtit.data.FingerLeftData
import com.fgtit.data.FingerRightData
import com.fgtit.data.FormData
import com.fgtit.device.Constants
import com.fgtit.device.FPModule
import com.fgtit.fpcore.FPMatch
import com.pearldrift.handsets.R
import com.skydoves.landscapist.glide.GlideImage
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable

fun RightFingerDialog(modifier: Modifier, changeState: MutableLiveData<String>, functionVariable:  (FingerRightData) -> Unit ){

    val context = LocalContext.current

    var mapBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }

    var iconState by remember {
        mutableStateOf(R.drawable.ic_fingerprint)
    }




    var bmpdata = ByteArray(Constants.RESBMP_SIZE)
    var bmpsize = 0
    var refdata = ByteArray(Constants.TEMPLATESIZE * 2)
    var refsize = 0
    var matdata = ByteArray(Constants.TEMPLATESIZE * 2)
    var matsize = 0
    val tmpdata = ByteArray(73728)
    var worktype = 0

    var textState by remember {
        mutableStateOf("Right Index")
    }

    changeState.observeForever{
        if(it.toBoolean()){
            textState = "Right Index"
            iconState = R.drawable.ic_fingerprint
        }
    }


    var matchTemp = remember {
        mutableStateOf<ByteArray?>(null)
    }

    val dialogState = rememberMaterialDialogState()
    var lottieState by remember {
        mutableStateOf("")
    }

    var counter = remember {
        mutableStateOf(0)
    }

    var fingerState by remember {
        mutableStateOf("")
    }



    var fpm = FingerSensor()

    @SuppressLint("HandlerLeak")
    val mHandler: Handler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            Log.d("user", "${msg.what}")

            when (msg.what) {

                Constants.FPM_DEVICE -> when (msg.arg1) {

                }

                Constants.FPM_LIFT ->  {
                    fingerState = "Lift Finger"
                    //                    playFinger.value = "true"
                    lottieState = "true"
                }
                Constants.FPM_PLACE -> {
                    fingerState = "Place Finger"
                    lottieState = "false"
                }

                Constants.FPM_GENCHAR -> {
                    lottieState = "false"
                    counter.value ++

                    matsize = fpm.GetTemplateByGen(matdata)

                    if(counter.value == 1){
                        fpm.RestartTask(true)
                        fingerState =  "Wait Regenerating"

                        matchTemp.value = matdata
                    }

                    if(counter.value == 2){

                        if (msg.arg1 == 1) {
                            fingerState =  "Generate Template OK"


                            var temp = matchTemp.value as ByteArray
//                            FPMatch.getInstance().MatchTemplate(model, tmp) > 60
                            var tmpsize = matchTemp.value?.let { fpm.getSize(it) } as Int

                            if (FPMatch.getInstance().MatchTemplate(refdata, matdata) > 60) {
                                fingerState = "Match OK"
                                textState = "Completed"
                                iconState = R.drawable.ic_fingerprint_completed
                                functionVariable(FingerRightData(matdata, refsize))
                            }
                            else{
                                fingerState = "Match Fail"
                            }
                        }

                        counter.value = 0
                    }
                    refsize = fpm.GetTemplateByGen(refdata)

                }

                Constants.FPM_NEWIMAGE -> {

                    bmpsize = fpm.GetBmpImage(bmpdata)
                    val bm1 = BitmapFactory.decodeByteArray(bmpdata, 0, bmpsize)

                    mapBitmap.value = bm1
                    val inpdata = ByteArray(73728)
                    val inpsize = 73728
                    System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize)

                }
            }
        }
    }

    fpm.defineFpm(context, mHandler)



    MaterialDialog(dialogState = dialogState,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
        onCloseRequest = {
            fpm.closeDevice()
        },
        buttons = {
            positiveButton("Save")
            negativeButton("Cancel", onClick = {
                dialogState.hide()
                fpm.closeDevice()
            })
            negativeButton("Re-instate", onClick = {

                dialogState.show()

            })
//                                                    isWorking
        }) {
        customView {

            Box(
                modifier = Modifier
                    .fillMaxWidth(1.5f).padding(horizontal = 30.dp)
                    .height(340.dp), contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Right Finger Print",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(Modifier.fillMaxWidth(1f))
                    Spacer(modifier = Modifier.height(20.dp))

                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.scanner_loading)
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        isPlaying = lottieState.toBoolean()
                    )

                    Box(
                        modifier = Modifier.size(
                            height = 180.dp,
                            width = 180.dp
                        ),
                        contentAlignment = Alignment.Center
                    ) {


//                        var bm1 = BitmapFactory.decodeByteArray(
//                            bitmapFingerLeft.value!!.bmpdata,
//                            0,
//                            bitmapFingerLeft.value!!.bmpsize
//                        )
//                        if (workPlace.value!!.equals("right")) {
//                            bm1 = BitmapFactory.decodeByteArray(
//                                bitmapFingerRight.value!!.bmpdata,
//                                0,
//                                bitmapFingerRight.value!!.bmpsize
//                            )
//                        }

                        GlideImage(
                            imageModel = mapBitmap.value,
                            // Crop, Fit, Inside, FillHeight, FillWidth, None
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(
                                    height = 130.dp,
                                    width = 130.dp
                                )
                                .rotate(360f),
                            // shows a placeholder while loading the image.
                            placeHolder = ImageVector.vectorResource(
                                id = R.drawable.ic_fingerprint__2
                            ),
                            // shows an error ImageBitmap when the request failed.
                            error = ImageVector.vectorResource(
                                id = R.drawable.ic_fingerprint__2
                            ),
                        )

                        LottieAnimation(
                            composition,
                            progress,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .fillMaxHeight(1f),
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = fingerState,
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 17.sp
                            )
                        )
                    }


                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        TextButton(
            onClick = {
                fpm.openDevice()
                fpm.GenerateTemplate(context, 1)
                dialogState.show()

            }, modifier = Modifier
                .padding(vertical = 10.dp)
                .height(200.dp)
                .width(200.dp)
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
                Image(
                    painter = painterResource(id = iconState),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(1f),
                    contentDescription = "finger"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(
                            horizontal = 25.dp,
                            vertical = 10.dp
                        )
                        .size(
                            width = 120.dp,
                            height = 40.dp
                        )
                        .background(Color.White)
                ) {
                    Text(
                        text = textState,
                        style = TextStyle(
                            fontSize = 17.sp,
                            color = Color.Black
                        ),
                    )
                }
            }
        }
    }

}