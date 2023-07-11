package com.pearldrift.handsets

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.compose.*
import com.fgtit.data.FingerPrintLeft
import com.fgtit.data.FingerPrintRight
import com.fgtit.data.FingerRightData
import com.fgtit.device.Constants
import com.fgtit.device.FPModule
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.EnrolmentModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.parse.*
import com.pearldrift.handsets.ui.main.components.SelectableTextField
import com.pearldrift.handsets.ui.main.components.categoryOptions
import com.pearldrift.handsets.ui.main.components.departmentsOptions
import com.pearldrift.handsets.ui.main.components.positionOptions
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.pearldrift.handsets.util.DataUtils
import com.pearldrift.handsets.util.ExtApi
import com.shashank.sony.fancytoastlib.FancyToast
import com.skydoves.landscapist.glide.GlideImage
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import tech.devscast.validable.EmailValidable
import tech.devscast.validable.NotEmptyValidable
import tech.devscast.validable.withValidable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EnrolmentActivity : ComponentActivity() {

    private val fpm = FPModule()


    private val bmpdata = ByteArray(Constants.RESBMP_SIZE)
    private var bmpsize = 0
    private val refdata = ByteArray(Constants.TEMPLATESIZE * 2)
    private var refsize = 0
    private val matdata = ByteArray(Constants.TEMPLATESIZE * 2)
    private var matsize = 0
    private var finger = MutableLiveData("")
    private var workPlace = MutableLiveData("")
    private var LeftGreen = MutableLiveData("")
    private var RightGreen = MutableLiveData("")
    private var fingerImageLeft = MutableLiveData(FingerPrintLeft(ByteArray(0), ByteArray(0), 0, 0))
    private var fingerImageRight = MutableLiveData(FingerPrintRight(ByteArray(0), ByteArray(0), 0, 0))
    private var stateChange = MutableLiveData("")

    private val model1 = ByteArray(512)
    private val model2 = ByteArray(512)

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        fpm.InitMatch()
        fpm.SetTimeOut(Constants.TIMEOUT_LONG)
        fpm.SetContextHandler(this, mHandler)
        fpm.SetLastCheckLift(true)

        setContent {

            var context = LocalContext.current
            val scrollState = rememberScrollState()
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            var fingerPlaced by remember {
                mutableStateOf("false")
            }

            var dialogTitle by remember {
                mutableStateOf("Finger Print")
            }



            var leftGreenRes by remember {
                mutableStateOf("false")
            }

            var rightGreenRes by remember {
                mutableStateOf("false")
            }

            LeftGreen.observeForever {
                leftGreenRes = it
            }

            RightGreen.observeForever {
                rightGreenRes = it
            }



            val bitmapFingerRight = remember {
                mutableStateOf<FingerPrintRight?>(
                    FingerPrintRight(
                        ByteArray(0),
                        ByteArray(0),
                        0,
                        0
                    )
                )
            }

            val bitmapFingerLeft = remember {
                mutableStateOf<FingerPrintLeft?>(
                    FingerPrintLeft(
                        ByteArray(0),
                        ByteArray(0),
                        0,
                        0
                    )
                )
            }

            fingerImageLeft.observeForever { observer ->
                bitmapFingerLeft.value = observer
            }

            fingerImageRight.observeForever { observer ->
                bitmapFingerRight.value = observer
            }

            stateChange.observeForever { observer ->
                fingerPlaced = observer.toString()
            }

            // form data

            var textName = remember() {
                NotEmptyValidable()
            }

            var textUniqueNo = remember() {
                NotEmptyValidable()
            }

            var birthdate = remember {
                mutableStateOf("1999-${month}-${day}")
            }
            var username = remember {
                NotEmptyValidable()
            }

            var mobile = remember {
                NotEmptyValidable()
            }

            var email = remember {
                EmailValidable()
            }

            var cardData by rememberSaveable() {
                mutableStateOf("")
            }
            var password = remember {
                NotEmptyValidable()
            }
            var cameraImageString by rememberSaveable { mutableStateOf("") }
            var categoryOptionText by remember { mutableStateOf(categoryOptions[0]) }
            val genderOptions = listOf("Male", "Female")
            var genderOptionText by remember { mutableStateOf(genderOptions[0]) }
            var workPositionOptionText by remember { mutableStateOf(positionOptions[0]) }
            var departmentsOptionText by remember { mutableStateOf(departmentsOptions[0]) }
            val coroutineScope = rememberCoroutineScope()

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

            BASHandsetsCompatiblektTheme() {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth(1f)
                        .scrollable(
                            orientation = Orientation.Vertical,
                            interactionSource = null,
                            state = scrollState
                        )
                ) {
                    Scaffold(
                        topBar = {
                            TopBarWithBack(title = "Staff Enrolment") {
                                finish()
                            }
                        },
                        content = ({
                            Box(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(1f)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth(1f)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        modifier = Modifier.padding(bottom = 10.dp),
                                        text = "Staff information",
                                        style = TextStyle(fontSize = 19.sp)
                                    )
                                    Divider(
                                        thickness = 2.dp,
                                        color = colorResource(id = R.color.gray)
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    TextField(
                                        value = textName.value,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words
                                        ),
                                        isError = textName.hasError(),
                                        onValueChange = { textName.value = it },
                                        label = {
                                            Text(
                                                "Fullname",
                                                style = TextStyle(fontSize = 17.sp)
                                            )
                                        },
                                        textStyle = TextStyle(fontSize = 17.sp),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth(1f)
                                            .padding(vertical = 10.dp)
                                    )
                                    AnimatedVisibility(visible = textName.hasError()) {
                                        Text(
                                            text = textName.errorMessage ?: "",
                                            modifier = Modifier.fillMaxWidth(),
                                            style = LocalTextStyle.current.copy(
                                                color = MaterialTheme.colors.error,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        SelectableTextField(modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 10.dp),
                                            onValueChange = {

                                            },
                                            label = "Birthdate",
                                            textValue = birthdate.value,
                                            onClick = {
                                                val dpd = DatePickerDialog(
                                                    context,
                                                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                                        // Display Selected date in TextView
                                                        birthdate.value =
                                                            "${year}-${month}-${dayOfMonth}"
                                                    },
                                                    1999,
                                                    month,
                                                    day
                                                )
                                                dpd.show()
                                            })

                                        Spacer(modifier = Modifier.width(15.dp))

                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            var expanded by remember { mutableStateOf(false) }
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = {
                                                    expanded = !expanded
                                                }
                                            ) {
                                                TextField(
                                                    readOnly = true,
                                                    value = departmentsOptionText,
                                                    onValueChange = { },
                                                    label = {
                                                        Text(
                                                            "Faculties and Departments",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = expanded
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp),
                                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = {
                                                        expanded = false
                                                    }
                                                ) {
                                                    departmentsOptions.forEach { selectionOption ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                departmentsOptionText =
                                                                    selectionOption
                                                                expanded = false
                                                            }
                                                        ) {
                                                            Text(text = selectionOption)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    /// department

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            var expanded by remember { mutableStateOf(false) }
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = {
                                                    expanded = !expanded
                                                }
                                            ) {
                                                TextField(
                                                    readOnly = true,
                                                    value = workPositionOptionText,
                                                    onValueChange = { },
                                                    label = {
                                                        Text(
                                                            "Work Position",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = expanded
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp),
                                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = {
                                                        expanded = false
                                                    }
                                                ) {
                                                    positionOptions.forEach { selectionOption ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                workPositionOptionText =
                                                                    selectionOption
                                                                expanded = false
                                                            }
                                                        ) {
                                                            Text(text = selectionOption)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(15.dp))
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            var expanded by remember { mutableStateOf(false) }
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = {
                                                    expanded = !expanded
                                                }
                                            ) {
                                                TextField(
                                                    readOnly = true,
                                                    value = categoryOptionText,
                                                    onValueChange = { },
                                                    label = {
                                                        Text(
                                                            "Staff Category",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = expanded
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp),
                                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = {
                                                        expanded = false
                                                    }
                                                ) {
                                                    categoryOptions.forEach { selectionOption ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                categoryOptionText = selectionOption
                                                                expanded = false
                                                            }
                                                        ) {
                                                            Text(text = selectionOption)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            var expanded by remember { mutableStateOf(false) }

                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = {
                                                    expanded = !expanded
                                                }
                                            ) {

                                                TextField(
                                                    readOnly = true,
                                                    value = genderOptionText,
                                                    onValueChange = { },
                                                    label = {
                                                        Text(
                                                            "Gender",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = expanded
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp),
                                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                                )
                                                ExposedDropdownMenu(
                                                    expanded = expanded,
                                                    onDismissRequest = {
                                                        expanded = false
                                                    }
                                                ) {
                                                    genderOptions.forEach { selectionOption ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                genderOptionText = selectionOption
                                                                expanded = false
                                                            }
                                                        ) {
                                                            Text(text = selectionOption)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(15.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {
                                            TextField(
                                                value = textUniqueNo.value,
                                                isError = textUniqueNo.hasError(),
                                                onValueChange = { textUniqueNo.value = it },
                                                label = {
                                                    Text(
                                                        "Unique ID no",
                                                        style = TextStyle(fontSize = 17.sp)
                                                    )
                                                },
                                                textStyle = TextStyle(fontSize = 17.sp),
                                                singleLine = true,
                                                modifier = Modifier
                                                    .fillMaxWidth(1f),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                            )
                                        }
                                    }

                                    //User login

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth(1f)
                                            ) {
                                                TextField(
                                                    value = username.value,
                                                    isError = username.hasError(),
                                                    onValueChange = { username.value = it },
                                                    label = {
                                                        Text(
                                                            "Username",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                                                )

                                                AnimatedVisibility(visible = username.hasError()) {

                                                    Text(
                                                        text = username.errorMessage ?: "",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        style = LocalTextStyle.current.copy(
                                                            color = MaterialTheme.colors.error,
                                                            fontSize = 15.sp
                                                        )
                                                    )

                                                }
                                            }


                                        }
                                        Spacer(modifier = Modifier.width(15.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {
                                            var passwordVisible by rememberSaveable {
                                                mutableStateOf(
                                                    false
                                                )
                                            }

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth(1f)
                                            ) {
                                                TextField(
                                                    value = password.value,
                                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                                    onValueChange = { password.value = it },
                                                    label = {
                                                        Text(
                                                            "Password",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                                    trailingIcon = {

                                                        val image = if (passwordVisible)
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.ic_visibility_fill),
                                                                contentDescription = "visible"
                                                            )
                                                        else Icon(
                                                            painter = painterResource(id = R.drawable.ic_visibility_off_fill),
                                                            contentDescription = "visible"
                                                        )

                                                        // Please provide localized description for accessibility services
                                                        val description =
                                                            if (passwordVisible) "Hide password" else "Show password"

                                                        IconButton(onClick = {
                                                            passwordVisible = !passwordVisible
                                                        }) {
                                                            image
                                                        }
                                                    }

                                                )

                                                AnimatedVisibility(visible = password.hasError()) {

                                                    Text(
                                                        text = password.errorMessage ?: "",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
                                                    )

                                                }
                                            }

                                        }
                                    }

                                    //contact info

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth(1f)
                                            ) {
                                                TextField(
                                                    value = email.value,
                                                    isError = email.hasError(),
                                                    onValueChange = { email.value = it },
                                                    label = {
                                                        Text(
                                                            "Email",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                                )


                                                AnimatedVisibility(visible = email.hasError()) {

                                                    Text(
                                                        text = email.errorMessage ?: "",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        style = LocalTextStyle.current.copy(
                                                            color = MaterialTheme.colors.error,
                                                            fontSize = 15.sp
                                                        )
                                                    )

                                                }
                                            }


                                        }
                                        Spacer(modifier = Modifier.width(15.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth(1f)
                                            ) {

                                                TextField(
                                                    value = mobile.value,
                                                    isError = mobile.hasError(),
                                                    onValueChange = { mobile.value = it },
                                                    label = {
                                                        Text(
                                                            "Mobile number",
                                                            style = TextStyle(fontSize = 17.sp)
                                                        )
                                                    },
                                                    textStyle = TextStyle(fontSize = 17.sp),
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .fillMaxWidth(1f),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                                )

                                                AnimatedVisibility(visible = mobile.hasError()) {

                                                    Text(
                                                        text = mobile.errorMessage ?: "",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        style = LocalTextStyle.current.copy(
                                                            color = MaterialTheme.colors.error,
                                                            fontSize = 15.sp
                                                        )
                                                    )

                                                }
                                            }

                                        }
                                    }


                                    val dialogState = rememberMaterialDialogState()

                                    MaterialDialog(dialogState = dialogState,
                                        properties = DialogProperties(
                                            dismissOnClickOutside = false,
                                            dismissOnBackPress = false,

                                            ),
                                        onCloseRequest = {
                                            fingerImageRight.value =
                                                FingerPrintRight(ByteArray(0), ByteArray(0), 0, 0)
                                            fingerImageLeft.value =
                                                FingerPrintLeft(ByteArray(0), ByteArray(0), 0, 0)
                                        },
                                        buttons = {
                                            positiveButton("Save")
                                            negativeButton("Cancel", onClick = {
                                                dialogState.hide()
                                            })
                                            negativeButton("Re-instate", onClick = {


                                                if (fpm.GenerateTemplate(2)) {
                                                    workPlace.value = workPlace.value
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Busy",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

                                                Toast.makeText(
                                                    context,
                                                    "Re-Instate done",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                dialogState.show()

                                            })
//                                                    isWorking
                                        }) {
                                        customView {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(5f)
                                                    .height(340.dp), contentAlignment = Center
                                            ) {

                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                                    Text(
                                                        text = dialogTitle,
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
                                                        isPlaying = fingerPlaced.toBoolean()
                                                    )

                                                    Box(
                                                        modifier = Modifier.size(
                                                            height = 200.dp,
                                                            width = 180.dp
                                                        ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        var bm1 = BitmapFactory.decodeByteArray(
                                                            bitmapFingerLeft.value!!.bmpdata,
                                                            0,
                                                            bitmapFingerLeft.value!!.bmpsize
                                                        )
                                                        if (workPlace.value!!.equals("right")) {
                                                            bm1 = BitmapFactory.decodeByteArray(
                                                                bitmapFingerRight.value!!.bmpdata,
                                                                0,
                                                                bitmapFingerRight.value!!.bmpsize
                                                            )
                                                        }

                                                        GlideImage(
                                                            imageModel = bm1,
                                                            // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                            contentScale = ContentScale.Fit,
                                                            modifier = Modifier
                                                                .size(
                                                                    height = 150.dp,
                                                                    width = 150.dp
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
                                                            text = finger.value.toString(),
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

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(
                                            contentAlignment = Alignment.Center, modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier.width(230.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                TextButton(
                                                    onClick = {
                                                        if (fpm.GenerateTemplate(2)) {
                                                            workPlace.value = "left"
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Busy",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }

                                                        dialogTitle = "Left Finger Print"
                                                        dialogState.show()

                                                    }, modifier = Modifier
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp)
                                                        .height(230.dp)
                                                        .clickable(
                                                            onClick = { /* Ignoring onClick */ },
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = rememberRipple(
                                                                bounded = true
                                                            )
                                                        )
                                                ) {
                                                    Box(
                                                        contentAlignment = Center,
                                                        modifier = Modifier.fillMaxSize(1f)
                                                    ) {


                                                        var leftIndex = "Left Index"
                                                        if (leftGreenRes.toBoolean() == true) {

                                                            leftIndex = "Completed"
                                                            Image(
                                                                painter = painterResource(id = R.drawable.ic_fingerprint_completed),
                                                                modifier = Modifier
                                                                    .fillMaxWidth(1f)
                                                                    .fillMaxHeight(1f),
                                                                contentDescription = "finger"
                                                            )

                                                        } else {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.ic_fingerprint),
                                                                modifier = Modifier
                                                                    .fillMaxWidth(1f)
                                                                    .fillMaxHeight(1f),
                                                                contentDescription = "finger"
                                                            )
                                                        }

                                                        Box(
                                                            contentAlignment = Center,
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
                                                                text = leftIndex,
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

                                        Spacer(modifier = Modifier.width(15.dp))
                                        Box(
                                            contentAlignment = Alignment.Center, modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {

                                            Box(
                                                modifier = Modifier.width(230.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                TextButton(
                                                    onClick = {
                                                        if (fpm.GenerateTemplate(2)) {
                                                            workPlace.value = "right"
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Busy",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }

                                                        dialogTitle = "Right Finger Print"

                                                        dialogState.show()

                                                    }, modifier = Modifier
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp)
                                                        .height(230.dp)
                                                        .clickable(
                                                            onClick = { /* Ignoring onClick */ },
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = rememberRipple(
                                                                bounded = true
                                                            )
                                                        )
                                                ) {
                                                    Box(
                                                        contentAlignment = Center,
                                                        modifier = Modifier.fillMaxSize(1f)
                                                    ) {

                                                        var rightIndex = "Right Index"

                                                        if (rightGreenRes.toBoolean() == true) {
                                                            rightIndex = "Completed"
                                                            Image(
                                                                painter = painterResource(id = R.drawable.ic_fingerprint_completed),
                                                                modifier = Modifier
                                                                    .fillMaxWidth(1f)
                                                                    .fillMaxHeight(1f),
                                                                contentDescription = "finger"
                                                            )

                                                        } else {
                                                            Image(
                                                                painter = painterResource(id = R.drawable.ic_fingerprint),
                                                                modifier = Modifier
                                                                    .fillMaxWidth(1f)
                                                                    .fillMaxHeight(1f),
                                                                contentDescription = "finger"
                                                            )
                                                        }

                                                        Box(
                                                            contentAlignment = Center,
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
                                                                text = rightIndex,
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
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Divider(
                                        thickness = 2.dp,
                                        color = colorResource(id = R.color.gray)
                                    )

                                    var nfcActivity = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartActivityForResult(),
                                        onResult = {
                                            if (it.resultCode == Activity.RESULT_OK) {
                                                var cardstr = it.data?.getStringExtra("cardstr")
                                                cardData = cardstr.toString()
                                            }
                                        }
                                    )

                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(
                                            contentAlignment = Alignment.Center, modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {

                                            Box(
                                                modifier = Modifier.width(230.dp),
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
                                                        .fillMaxWidth(1f)
                                                        .padding(vertical = 10.dp)
                                                        .height(230.dp)
                                                        .clickable(
                                                            onClick = { /* Ignoring onClick */ },
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = rememberRipple(
                                                                bounded = true
                                                            )
                                                        )
                                                ) {
                                                    Box(
                                                        contentAlignment = Center,
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
                                                                contentAlignment = Center,
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

                                        Spacer(modifier = Modifier.width(15.dp))

                                        Box(
                                            contentAlignment = Alignment.Center, modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp)
                                                .weight(1f)
                                        ) {

                                        }
                                    }
                                    Column() {
                                        Button(
                                            onClick = {
                                                var  bytes1 = ByteArray(model1.size)
                                                var  bytes2 = ByteArray(model2.size)

                                                System.arraycopy(
                                                    bytes1,
                                                    0,
                                                    bytes1,
                                                    0,
                                                    model1.size
                                                )

                                                System.arraycopy(
                                                    model2,
                                                    0,
                                                    bytes2,
                                                    0,
                                                    model2.size
                                                )

                                                val current = LocalDateTime.now()

                                                var formatter =
                                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                                var formattedDate = current.format(formatter)

                                                Log.d("user", "${String(bytes1)}")
                                                withValidable(
                                                    username,
                                                    textName,
                                                    mobile,
                                                    email,
                                                    password
                                                ) {
                                                    // will be executed if all fields are valid
                                                    var uuid = UUID.randomUUID()
                                                    var formData = EnrolmentModel(
                                                        uuid = uuid.toString(),
                                                        fullname = textName.value,
                                                        birthdate = birthdate.value,
                                                        username = username.value,
                                                        fac_a_department = departmentsOptionText,
                                                        work_position = workPositionOptionText,
                                                        staff_category = categoryOptionText,
                                                        gender = genderOptionText,
                                                        unique_id_no = textUniqueNo.value,
                                                        mobile = mobile.value,
                                                        enrolled_date = formattedDate,
                                                        email = email.value,
                                                        password = password.value,
                                                        nfc_card_code = cardData,
                                                        image = cameraImageString,
                                                        left_fingerprint = bytes1,
                                                        left_inpsize = model1.size,
                                                        right_inpsize = model1.size,
                                                        right_fingerprint = bytes2,

                                                        )

                                                    if (cameraImageString!!.isEmpty()) {
                                                        Toast.makeText(
                                                            context,
                                                            "Please complete Finger capture",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else if (cameraImageString!!.isEmpty()) {
                                                        Toast.makeText(
                                                            context,
                                                            "Please complete Image capture",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
//                                                              val decodedBytes = Base64.getDecoder().decode(formData.nfc_card_code)
                                                        val enrolBox =
                                                            ObjectBox.store.boxFor(EnrolmentModel::class.java)
                                                        enrolBox.put(formData)


                                                        val StaffEnrolment =
                                                            ParseObject("StaffEnrolment")

                                                        StaffEnrolment.put("uuid", uuid.toString())
                                                        StaffEnrolment.put(
                                                            "fullname",
                                                            formData?.fullname.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "username",
                                                            formData?.username.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "birthdate",
                                                            formData?.birthdate.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "fac_a_department",
                                                            formData?.fac_a_department.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "work_position",
                                                            formData?.work_position.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "staff_category",
                                                            formData?.staff_category.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "gender",
                                                            formData?.gender.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "unique_id_no",
                                                            formData?.unique_id_no.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "mobile",
                                                            formData?.mobile.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "email",
                                                            formData?.email.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "enrolled_date",
                                                            formData?.enrolled_date.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "password",
                                                            formData?.password.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "nfc_card_code",
                                                            formData?.nfc_card_code.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "left_fingerprint",
                                                            ExtApi.BytesToBase64(
                                                                DataUtils.compress(formData?.left_fingerprint),
                                                                0
                                                            )
                                                        )
                                                        StaffEnrolment.put(
                                                            "left_inpsize",
                                                            formData?.left_inpsize.toString()
                                                        )
                                                        StaffEnrolment.put(
                                                            "right_fingerprint",
                                                            ExtApi.BytesToBase64(
                                                                DataUtils.compress(formData?.right_fingerprint),
                                                                0
                                                            )
                                                        )
                                                        StaffEnrolment.put(
                                                            "right_inpsize",
                                                            formData?.right_inpsize.toString()
                                                        )

                                                        StaffEnrolment.put(
                                                            "by_staff",
                                                            ParseUser.getCurrentUser()
                                                        );

                                                        var inputStream =
                                                            context?.contentResolver.openInputStream(
                                                                Uri.parse(cameraImageString)
                                                            )
                                                        var byteArray = inputStream?.readBytes()


                                                        val file =
                                                            ParseFile("staff_image.jpeg", byteArray)
                                                        StaffEnrolment.put("staff_image", file)
                                                        var enrol =
                                                            StaffEnrolment.saveInBackground()


                                                        coroutineScope.launch {
                                                            scrollState.scrollTo(0)
                                                        }

                                                        mHandler = Handler(getMainLooper())
                                                        mHandler.postDelayed({

//
                                                            FancyToast.makeText(
                                                                context,
                                                                "Data saved and added to queue.",
                                                                FancyToast.LENGTH_LONG,
                                                                FancyToast.SUCCESS,
                                                                true
                                                            ).show();


                                                            fingerImageLeft.value = FingerPrintLeft(
                                                                ByteArray(0),
                                                                ByteArray(0),
                                                                0,
                                                                0
                                                            )
                                                            LeftGreen.value = ""
                                                            RightGreen.value = ""
                                                            textName.value = ""
                                                            birthdate.value = ""
                                                            username.value = ""
                                                            textUniqueNo.value = ""
                                                            mobile.value = ""
                                                            email.value = ""
                                                            password.value = ""
                                                            cardData = ""
                                                            cameraImageString = ""
                                                        }, 1000)

                                                        // Get the user from a non-authenticated manner
                                                        // Get the user from a non-authenticated manner

                                                        val query = ParseUser.getQuery()
                                                        query.getInBackground(
                                                            StaffEnrolment.getObjectId(),
                                                            object : GetCallback<ParseUser?> {

                                                                override fun done(
                                                                    `object`: ParseUser?,
                                                                    e: ParseException?
                                                                ) {


                                                                }
                                                            })
                                                    }

                                                }

                                            },
                                            modifier = Modifier.fillMaxWidth(10f),
                                            contentPadding = PaddingValues(10.dp)
                                        ) {
                                            Text(
                                                text = "Submit Record",
                                                style = TextStyle(fontSize = 17.sp)
                                            )
                                        }


                                    }
                                }

                            }
                        }),
                        bottomBar = {

                        }
                    )
                }
            }


        }
    }

    override fun onResume() {
        fpm.ResumeRegister()
        fpm.OpenDevice()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        fpm.Cancle()
        super.onDestroy()
    }

    override fun onStop() {
//        fpm.PauseUnRegister()
//        fpm.CloseDevice()
        super.onStop()
    }


    @SuppressLint("HandlerLeak")
    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {

                Constants.FPM_DEVICE -> when (msg.arg1) {

                }

                Constants.FPM_LIFT ->  {
                    finger.value = "Lift Finger"
                    //                    playFinger.value = "true"
                    stateChange.value = "true"
                }
                Constants.FPM_PLACE -> {
                    finger.value = "Place Finger"
                    stateChange.value = "false"
                }

                Constants.FPM_GENCHAR -> {
                    try {
                        if (msg.arg1 == 1) {
                            finger.value = "Generate Template OK"
                            matsize = fpm.GetTemplateByGen(matdata)

                            refsize = fpm.GetTemplateByGen(refdata)

                            if (fpm.MatchTemplate(
                                    refdata,
                                    refsize,
                                    matdata,
                                    matsize,
                                    60
                                )
                            ){
                                finger.value ="Match OK"

                                if(workPlace.value!!.equals("left")){
                                    LeftGreen.value = "true"
                                }

                                if(workPlace.value!!.equals("right")){
                                    RightGreen.value = "true"
                                }

                                stateChange.value = "false"

                                if(workPlace.value!!.equals("left")){
                                    /// Left Finger
                                    System.arraycopy(refdata, 0, this@EnrolmentActivity.model1, 0, 512)
                                    Log.d("user", "se -> ${String(refdata)}")
                                }
                                if(workPlace.value!!.equals("right")){
                                    /// Right Finger
                                    System.arraycopy(refdata, 0, this@EnrolmentActivity.model2, 0, 512)
                                }

                            }else{

                                stateChange.value = "false"
                                finger.value ="Match Fail"


                            }
                        }
                        else{
                            finger.value = "Generate Template Fail"
                        }
                    }
                    catch (e: Exception){


                    }

                }

                Constants.FPM_NEWIMAGE -> {
                    try {
                        bmpsize = fpm.GetBmpImage(bmpdata)
                        val inpdata = ByteArray(73728)
                        val inpsize = 73728
                        System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize)

                        if(workPlace.value!!.equals("left")){
                            /// Left Finger
                            fingerImageLeft.value = FingerPrintLeft(bmpdata, inpdata, inpsize, bmpsize)
                        }
                        if(workPlace.value!!.equals("right")){
                            /// Right Finger
                            fingerImageRight.value = FingerPrintRight(bmpdata, inpdata, inpsize, bmpsize)
                        }


                    }
                    catch (e: Exception){
                    }

                }
            }
        }
    }
}