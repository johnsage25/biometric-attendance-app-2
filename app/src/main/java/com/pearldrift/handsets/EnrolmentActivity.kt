package com.pearldrift.handsets

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.fgtit.data.FingerLeftData
import com.fgtit.data.FingerRightData
import com.fgtit.data.FormData
import com.fgtit.fpcore.ObjectBox
import com.fgtit.fpcore.StaffAuthClass
import com.fgtit.model.EnrolmentModel
import com.kswafx.iamtoast.IamToast
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import com.pearldrift.handsets.ui.main.components.*
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.pearldrift.handsets.util.DataHttpHandler
import com.pearldrift.handsets.util.ExtApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.devscast.validable.EmailValidable
import tech.devscast.validable.NotEmptyValidable
import tech.devscast.validable.withValidable
import java.text.SimpleDateFormat
import java.util.*


class EnrolmentActivity : ComponentActivity() {

    var stateChange = MutableLiveData<String>("")
    private val REQUEST_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //checking the permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, PERMISSIONS_STORAGE,
                    REQUEST_PERMISSION_CODE
                )
            }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build()
        )

        val staffId = StaffAuthClass().readFromSharedPreferences("userId", this)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val scrollState = rememberScrollState()
            var formdata by remember {  mutableStateOf(FormData(
                textFullname = NotEmptyValidable(),
                emailField = EmailValidable(),
                mobiletext = NotEmptyValidable(),
                genderOptionText = "",
                categoryOptionText = "",
                workPositionOptionText = "",
                birthdate = "",
                departmentsOptionText = "",
                uniqueNotext = "")) }
            var finger_l_data by remember {
                mutableStateOf(FingerLeftData(inpdata = ByteArray(0), 0))
            }

            var finger_r_data by remember {
                mutableStateOf(FingerRightData(inpdata = ByteArray(0), 0))
            }

            var nfc_data by remember {
                mutableStateOf("")
            }

            var camera_data by remember {
                mutableStateOf("")
            }

            var resetGreen by remember {
                mutableStateOf<Int>(0)
            }


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
                        TopBarWithBack(title = "Staff Enrolment") {
                            finish()
                        }
                    }, bottomBar = {

                    },
                    content = ({
                        Column(modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                            .fillMaxWidth()
                            ) {

                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                modifier = Modifier.padding(bottom = 10.dp),
                                text = "Staff information",
                                style = TextStyle(fontSize = 19.sp),
                            )
                            Divider(
                                thickness = 2.dp,
                                color = colorResource(id = R.color.gray)
                            )
                            var event = remember {
                                mutableStateOf("false")
                            }

                            TextInputComponent(event = event){
                                formdata = it
                            }


                           Row(modifier = Modifier
                               .fillMaxWidth()
                               .height(240.dp)) {
                               LeftFingerDialog(

                                   Modifier
                                       .fillMaxSize(1f)
                                       .weight(1f),
                                   changeState = stateChange){

                                   finger_l_data = it

                               }
                               RightFingerDialog(
                                   Modifier
                                       .fillMaxSize(1f)
                                       .weight(1f)
                                       .width(230.dp),
                                   changeState = stateChange
                               ){

                                   finger_r_data = it

                               }
                           }

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)){

                                NfcFunction(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    changeState = stateChange
                                ){

                                    Log.d("user", "cardstr ${it}")
                                    nfc_data = it.toString()
                                }
                                CameraComponent(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                    changeState = stateChange){

                                    camera_data = it
                                }
                            }

                            Button(onClick = {

                                withValidable(formdata.emailField, formdata.mobiletext,  formdata.textFullname) {

                                    if(finger_l_data.inpdata.isEmpty() || finger_r_data.inpdata.isEmpty()){
                                        Toast.makeText(
                                            this@EnrolmentActivity,
                                            "Please complete Finger capture",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        return@withValidable
                                    }

                                    if(camera_data.isEmpty()){

                                        Toast.makeText(
                                            this@EnrolmentActivity,
                                            "Please complete Image capture",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@withValidable
                                    }


                                    val uuid = UUID.randomUUID().toString()


                                    val sm = SimpleDateFormat("yyyy-MM-dd")

                                    val birthdate = sm.parse(formdata.birthdate)
                                    var timestamp = Date()
                                    var formData = EnrolmentModel(
                                        uuid = uuid,
                                        fullname = formdata.textFullname.value,
                                        birthdate = birthdate,
                                        username = "",
                                        fac_a_department = formdata.departmentsOptionText,
                                        work_position = formdata.workPositionOptionText,
                                        staff_category = formdata.categoryOptionText,
                                        gender = formdata.genderOptionText,
                                        unique_id_no = formdata.uniqueNotext,
                                        mobile = formdata.mobiletext.value,
                                        enrolled_date = timestamp,
                                        email = formdata.emailField.value,
                                        password = "",
                                        objectId = "",
                                        nfc_card_code = nfc_data,
                                        image = camera_data,
                                        left_fingerprint = ExtApi.BytesToBase64(
                                            finger_l_data.inpdata,
                                            0
                                        ),
                                        left_inpsize = finger_l_data.inpsize,
                                        right_inpsize = finger_r_data.inpsize,
                                        right_fingerprint =  ExtApi.BytesToBase64(
                                            finger_r_data.inpdata,
                                            0
                                        ),
                                        by_staff = staffId
                                    )

                                    val enrolBox =
                                        ObjectBox.store.boxFor(EnrolmentModel::class.java)


                                    var filename = Uri.parse(camera_data).pathSegments.last()

                                    GlobalScope.launch(Dispatchers.IO) {

                                        try {
                                            DataHttpHandler().sendStaffData(this@EnrolmentActivity, filename, formData)

                                            enrolBox.put( formData )
                                        }
                                        catch (e: Exception){
                                            Log.e("user-log", "$e")
                                        }
                                    }

                                    coroutineScope.launch {
                                        scrollState.scrollTo(0)

                                        camera_data = ""
                                        finger_l_data = FingerLeftData(inpdata = ByteArray(0), 0)
                                        finger_r_data = FingerRightData(inpdata = ByteArray(0), 0)
                                        stateChange.value = "true"

                                        formdata = FormData(
                                            textFullname = NotEmptyValidable(),
                                            emailField = EmailValidable(),
                                            mobiletext = NotEmptyValidable(),
                                            genderOptionText = "",
                                            categoryOptionText = "",
                                            workPositionOptionText = "",
                                            birthdate = "",
                                            departmentsOptionText = "",
                                            uniqueNotext = "")
                                    }

                                    IamToast.success(this@EnrolmentActivity, "Data saved and added to queue.",  IamToast.GRAVITY_BOTTOM,
                                        IamToast.LONG_DURATION)

                                    event.value = "true"

                                }


                            },  modifier = Modifier.fillMaxWidth(20f),
                                contentPadding = PaddingValues(10.dp)) {
                                Text(
                                    text = "Submit Record",
                                    style = TextStyle(fontSize = 17.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }))
                }
            }
        }


    }


    companion object {
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}