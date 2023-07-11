package com.pearldrift.handsets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.datetimeutils.DateTimeUtils
import com.fgtit.data.*
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.Attendance
import com.fgtit.model.Attendance_
import com.fgtit.model.EnrolmentModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.parse.*
import com.pearldrift.handsets.service.DownloadingStaff
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.pearldrift.handsets.util.DownloadImage
import com.pearldrift.handsets.util.ExtApi
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.result.Result
import com.kswafx.iamtoast.IamToast
import com.pearldrift.handsets.util.DataHttpHandler
import com.shashank.sony.fancytoastlib.FancyToast
import downloadImageWithVolleyAndSave

class SyncActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContent {
            BASHandsetsCompatiblektTheme {
                // A surface container using the 'background' color from the theme

                var disable_button by remember{
                    mutableStateOf<String>("true")
                }

                var playProgress by remember {
                    mutableStateOf("false")
                }

                var process_text by remember{
                    mutableStateOf("")
                }

                var progress by remember { mutableStateOf(0.01f) }
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                )

                /// service download



                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                  Scaffold(
                      topBar = {
                          TopBarWithBack(title = "Synchronize Cloud Data"){
                              finish()
                          }
                      }
                  ) {
                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

                            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cloud_sync))
                            val progress2 by animateLottieCompositionAsState(composition = composition,
                                iterations = LottieConstants.IterateForever,
                                restartOnPlay= true,
                                isPlaying = playProgress.toBoolean() )
                            Spacer(modifier = Modifier.height(100.dp))
                            Text(text = "Import and Export data", style =  MaterialTheme.typography.h4)

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp), contentAlignment = Alignment.Center) {

                                LottieAnimation(
                                    composition,
                                    progress2,
                                    modifier = Modifier.width(390.dp)
                                )


                            }

                            AnimatedVisibility(visible = playProgress.toBoolean(), enter = fadeIn(
                                // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                                initialAlpha = 0.4f
                            ), exit = fadeOut(
                                // Overwrites the default animation with tween
                                animationSpec = tween(durationMillis = 250)
                            )) {


                                Column(modifier = Modifier.height(80.dp), horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(text = process_text, fontSize = 30.sp)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    LinearProgressIndicator(progress = animatedProgress, Modifier.width(400.dp))
                                }

                            }

                           Column() {

                               Button(onClick = {

                                   playProgress = "true"
                                   disable_button = "false"
                                   process_text = "Uploading attendance...."

                                   val attendance = ObjectBox.store.boxFor(Attendance::class.java)
                                   var attenSI = attendance.query(Attendance_.attns_type.equal("signed_in")).build().find()
                                   var attenSO = attendance.query(Attendance_.attns_type.equal("signed_out")).build().find()

                                  GlobalScope.launch(Dispatchers.IO) {

                                      delay(200)
                                      try {


                                          progress = 1f / attenSI.size.toFloat()


                                          for ( (key, item) in attenSI.withIndex()){
                                              //// looping items for data



                                              var lp =  key.toFloat() / attenSI.size.toFloat()

                                              var json = Gson().toJson(item)


                                              Fuel.post("${getString(R.string.basApi)}/attendance").jsonBody(json).also {
                                                  var callback = it.awaitString()
                                                  Log.d("user-log", "${callback}")
                                              }

                                              progress = lp
                                          }

                                          delay(500)

                                          process_text = "Finalizing attendance...."

                                          progress = 1f / attenSO.size.toFloat()

                                          for ( (key, item) in attenSO.withIndex()){
                                              //// looping items for data

                                              var json = Gson().toJson(item)
                                              Log.d("user-log", "${json}")

                                              var lp =  key.toFloat() / attenSO.size.toFloat()

                                              Fuel.post("${getString(R.string.basApi)}/attendance").jsonBody(json)
                                                  .also {
                                                      var callback = it.awaitString()
                                                      Log.d("user-log", "${callback}")
                                                  }

                                              progress = lp
                                          }


                                          playProgress = "false"
                                          process_text = ""
                                          disable_button = "true"

                                      }catch (e: Exception){
                                          Log.d("user-log", "${e}")
                                      }
                                  }

                               }, enabled = disable_button.toBoolean(), contentPadding = PaddingValues(
                                   top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                                   modifier = Modifier.width(300.dp)) {
                                   Icon(
                                       Icons.Filled.Upload,
                                       contentDescription = null,
                                       modifier = Modifier.size(ButtonDefaults.IconSize)
                                   )
                                   Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                   Text("Upload Attendance")
                               }

                               Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                               Button(onClick = {

                                   playProgress = "true"
                                   disable_button = "false"
                                   process_text = "Uploading...."

                                   // Upload attendance and Enrolment enrolment
                                   val enrolment = ObjectBox.store.boxFor(EnrolmentModel::class.java)
                                   var idex = enrolment.query().build().find()

                                   GlobalScope.launch(Dispatchers.IO) {
                                        try {

                                            var index = 0

                                            progress = 1f / idex.size.toFloat()

                                            for ((key, it) in idex.withIndex()) {

                                                var lp =  key.toFloat() / idex.size.toFloat()


                                                val percent:Double = String.format("%.1f", (key.toDouble() / idex.size.toFloat()) * 100).toDouble()

                                                process_text = "Uploading....${percent}%"

                                                /// uploading staff enrolment
                                                try {

                                                    var filename = Uri.parse(it.image).pathSegments.last()

                                                    DataHttpHandler().sendStaffData(this@SyncActivity, filename, it)

                                                    if(it.uuid.equals(idex[idex.size -1].uuid)){
                                                        playProgress = "false"
                                                        process_text = ""
                                                        disable_button = "true"
                                                    }
                                                    progress = lp

                                                }catch(e: Exception){
                                                    Log.d("user-log", "${e.message}")
                                                    playProgress = "false"
                                                    process_text = ""
                                                    disable_button = "true"
                                                }



                                            }

                                            withContext(Dispatchers.Main){
                                                playProgress = "false"
                                                process_text = ""
                                                disable_button = "true"
                                            }

                                        }catch (e: Exception){
                                            playProgress = "false"
                                            process_text = ""
                                            disable_button = "true"
                                        }

                                   }

                               }, enabled = disable_button.toBoolean(), contentPadding = PaddingValues(
                                   top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                                   modifier = Modifier.width(300.dp)) {
                                   Icon(
                                       Icons.Filled.Upload,
                                       contentDescription = null,
                                       modifier = Modifier.size(ButtonDefaults.IconSize)
                                   )
                                   Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                   Text("Upload staff data")
                               }
                               Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                               Button(onClick = {

                                   playProgress = "true"
                                   disable_button = "false"
                                   process_text = "Downloading...."
                                   var counter = 0




                                   Fuel.get("${getString(R.string.basApi)}/getstaffs")
                                       .timeout(50000)
                                       .header("Content-Type" to "application/json")

                                       .responseString { request, response, result ->
                                           when (result) {

                                               is Result.Success -> {
                                                   val data = result.get()

                                                   var staffs = Gson().fromJson<StaffArrayObject>(
                                                       data,
                                                       StaffArrayObject::class.java)

                                                   GlobalScope.launch(Dispatchers.IO) {

                                                       val enrolBox =
                                                           ObjectBox.store.boxFor(EnrolmentModel::class.java)

                                                       progress = 1f / staffs.staff.size.toFloat()

                                                       for ((key, it) in staffs.staff.withIndex()){

                                                           var lp =
                                                               key.toFloat() / staffs.staff.size.toFloat();

                                                           val percent: Double =
                                                               String.format("%.1f",
                                                                   (key.toDouble() / staffs.staff.size.toFloat()) * 100)
                                                                   .toDouble()

                                                           process_text =
                                                               "Downloading....${percent}%"

                                                           DataHttpHandler().syncErrolmentData(this@SyncActivity,it)

                                                           if (counter >= staffs.staff.size) {
                                                               playProgress = "false"
                                                               process_text = ""
                                                               disable_button = "true"
                                                           }
                                                           progress = lp
                                                       }

                                                       withContext(Dispatchers.Main){
                                                           playProgress = "false"
                                                           process_text = ""
                                                           disable_button = "true"
                                                       }
                                                   }


                                               }
                                               is Result.Failure -> {
                                                   val error = result.getException()

                                                   playProgress = "false"
                                                   process_text = ""
                                                   disable_button = "true"
                                                   IamToast.error(this@SyncActivity, "Unable to reach server, you might be on a slow internet.", IamToast.GRAVITY_BOTTOM,
                                                       IamToast.LONG_DURATION)

                                               }
                                           }
                                       }

//                                   val intent = Intent(this@SyncActivity, DownloadingStaff::class.java)
//                                   this@SyncActivity?.startService(intent)


                               }, enabled = disable_button.toBoolean(), contentPadding = PaddingValues(
                                   top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                                   modifier = Modifier.width(300.dp)) {
                                   Icon(
                                       Icons.Filled.Download,
                                       contentDescription = null,
                                       modifier = Modifier.size(ButtonDefaults.IconSize)
                                   )
                                   Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                   Text("Download staff data")
                               }

                           }
                        }
                  }
                }
            }
        }
    }


//    fun getStaffValue(staff: StaffDataDo){
//
//
//             try {
//                 var imageData = ExtApi.uriToImage(staff.staff_image.url)
//
//
//                 var bitmap = ExtApi.saveImage(Glide.with(applicationContext)
//                     .asBitmap()
//                     .load("${staff.staff_image.url}") // sample image
//                     .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
//                     .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
//                     .submit()
//                     .get(),
//                     staff.staff_image.name,
//                     applicationContext
//                 )
//
//                 val enrolBox =
//                     ObjectBox.store.boxFor(EnrolmentModel::class.java)
//
//                 var formData = EnrolmentModel(
//                     uuid = staff.uuid,
//                     fullname = staff.fullname,
//                     birthdate = ExtApi.StrToDateFormat(),
//                     fac_a_department = staff.fac_a_department,
//                     work_position = staff.work_position,
//                     staff_category = staff.staff_category,
//                     gender = staff.gender,
//                     unique_id_no = staff.unique_id_no,
//                     username = staff.username,
//                     password = "",
//                     email = staff.email,
//                     mobile = staff.mobile,
//                     left_fingerprint = staff.left_fingerprint?.trim(),
//                     left_inpsize = 256,
//                     right_inpsize = 256,
//                     right_fingerprint = staff.right_fingerprint?.trim(),
//                     nfc_card_code = staff.nfc_card_code,
//                     enrolled_date = ExtApi.StrToDateFormat(staff.enrolled_date.iso.toString()),
//                     image = "${applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM)!!}/${staff.staff_image.name}",
//                     by_staff = ParseUser.getCurrentUser().objectId,
//                     objectId = staff.objectId,
//                     work_hour = staff?.work_hour,
//                 )
//
//                 enrolBox.put(formData)
//
//             }catch (e: Exception){
//                 Log.e("user-log", "${e}")
//             }
//
//    }


    fun formDate(date: Date?): String{
        if(date !== null){
            return DateTimeUtils.formatWithPattern(date, "yyyy-MM-dd HH:mm:ss")
        }
        else{
            return ""
        }
    }

    fun fileToBase64(url: String?): String {

        val filePath = ExtApi.IsFileExists(url?.replace("file:///", "//"))

        if(filePath){
            val imageValue = ExtApi.LoadDataFromFile(url?.replace("file:///", "//"))
            return ExtApi.BytesToBase64(imageValue, 0)
        }

        return ""
    }

    fun uploadStaff(StaffEnrolment: ParseObject, objectId: String, query: ParseQuery<ParseObject>): UploadException? {
        var response: UploadException? = null

        try {
            StaffEnrolment.saveInBackground().let {

//                response = UploadException(it, completed = true)
//                Log.d("user", "${ it }")
            }
        }
        catch (e: Exception){
        Log.d("user", "${e}")
//                query.getInBackground(objectId,
//                    object : GetCallback<ParseObject?> {
//                        override fun done(
//                            `object`: ParseObject?,
//                            e: ParseException?,
//                        ) {
//                            Log.d("user", "error: ${e}")
//
//                            var enrol =
//                                StaffEnrolment.saveInBackground()
//                            response = UploadException(e, completed = true)
//                        }
//
//                    })

        }

        return response
    }

    internal class DownloadReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == "GET_DOWNLOAD_COUNTER") {
                val progress = intent.getIntExtra("progress", 0)
                Log.d("user-log", "${progress}")
                // Show it in GraphView
            }
        }
    }
}