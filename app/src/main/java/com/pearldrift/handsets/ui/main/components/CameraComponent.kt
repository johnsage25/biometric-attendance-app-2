package com.pearldrift.handsets.ui.main.components

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.github.dhaval2404.imagepicker.ImagePicker
import com.pearldrift.handsets.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun CameraComponent(modifier: Modifier, changeState: MutableLiveData<String>, camera_prama: @Composable (image_string: String) ->Unit){
    val context = LocalContext.current
    var cameraImageString by rememberSaveable { mutableStateOf("") }
    camera_prama(image_string = cameraImageString)

    Log.d("user", "camera: ${cameraImageString}")

    changeState.observeForever{
        if(it.toBoolean()){
            cameraImageString =""
        }
    }


    var cameraActivity = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            try {
                val resultCode = it.resultCode
                if (resultCode == Activity.RESULT_OK) {
                    val fileUri = it.data?.data!!
                    cameraImageString = fileUri.toString()
                }
            } catch (e: Exception) {
            }
        }
    );

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = {
                // Load image picker
                val Cameraintent2 =
                    ImagePicker.with(context as Activity)
                        .compress(1024)         //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(
                            1080,
                            1080
                        )  //Final image resolution will be less than 1080 x 1080(Optional)
                        .crop()
                        .createIntent { intent ->
                            cameraActivity.launch(intent)
                        }


            }, modifier = Modifier
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

                GlideImage(
                    imageModel = cameraImageString,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(1f),
                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                    contentScale = ContentScale.Crop,
                    // shows a placeholder while loading the image.
                    placeHolder = ImageVector.vectorResource(
                        id = R.drawable.ic_camera_bas
                    ),
                    // shows an error ImageBitmap when the request failed.
                    error = ImageVector.vectorResource(
                        id = R.drawable.ic_camera_bas
                    ),
                )
            }
        }

    }
}