package com.pearldrift.handsets.ui.main.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fgtit.data.FormData
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import tech.devscast.validable.delegates.validableEmail
import tech.devscast.validable.delegates.validableNotEmpty
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TextInputComponent(event: MutableState<String>, functionVariable: @Composable (FormData) -> Unit ) {

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH) +1
    val day = c.get(Calendar.DAY_OF_MONTH)

    var departmentsOptionText by remember { mutableStateOf(departmentsOptions[0]) }
    var workPositionOptionText by remember { mutableStateOf(positionOptions[0]) }
    val genderOptions = listOf("Male", "Female")
    var genderOptionText by remember { mutableStateOf(genderOptions[0]) }
    var categoryOptionText by remember { mutableStateOf(categoryOptions[0]) }
    val textFullname by validableNotEmpty()
    val mobiletext by validableNotEmpty()
    var uniqueNotext by remember { mutableStateOf("") }
    val emailField by validableEmail()
    val workHours by validableNotEmpty()
    var enablError by remember {
        mutableStateOf("false")
    }

    if(event.value.toBoolean()){
        textFullname.value =""
        mobiletext.value =""
        uniqueNotext = ""
        emailField.value =""
        departmentsOptionText = departmentsOptions[0]
        workPositionOptionText = positionOptions[0]
        genderOptionText = genderOptions[0]
        categoryOptionText = categoryOptions[0]
        workHours
        /// reset error
        enablError = "false"
    }



    var context = LocalContext.current
    var birthdate = remember {
        mutableStateOf("1999-${month}-${day}")
    }
    val cal = Calendar.getInstance()


    Column(
        Modifier
            .fillMaxWidth()) {


       Box( modifier = Modifier
           .fillMaxWidth(1f)
           .padding(bottom = 10.dp)) {

           Column( modifier = Modifier.fillMaxWidth()) {

               var error_display = if( enablError.toBoolean() ) textFullname.hasError() else enablError.toBoolean()

               TextField(
                   value = textFullname.value,
                   isError = error_display,
                   singleLine = true,
                   onValueChange = { textFullname.value = it },
                   label = { Text("Fullname") },
                   keyboardOptions = KeyboardOptions(
                       capitalization = KeyboardCapitalization.Words
                   ),
                   placeholder = { Text("John Doe") },
                   modifier = Modifier
                       .fillMaxWidth(1f)
               )


               AnimatedVisibility(visible = error_display) {

                   Text(
                       text = textFullname.errorMessage ?: "",
                       modifier = Modifier.fillMaxWidth(),
                       style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
                   )

               }
           }

       }

       Row(modifier = Modifier
           .fillMaxWidth()
           .padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {

           var error_display = if( enablError.toBoolean()) emailField.hasError() else enablError.toBoolean()

           Column(
               modifier = Modifier
                   .fillMaxWidth(1f)
                   .weight(1f)
                   .padding(end = 10.dp),
           ) {


               TextField(
                   value = emailField.value,
                   isError = error_display,
                   singleLine = true,
                   onValueChange = { emailField.value = it },
                   label = { Text("Email") },
                   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                   placeholder = { Text("example@pearldrift.com") },
                   modifier = Modifier
                       .fillMaxWidth(1f)
               )

               AnimatedVisibility(visible = error_display) {

                   Text(
                       text = emailField.errorMessage ?: "",
                       modifier = Modifier.fillMaxWidth(),
                       style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
                   )

               }

           }


           Column( modifier = Modifier
               .fillMaxWidth(1f)
               .weight(1f)) {

               var error_display = if( enablError.toBoolean() ) mobiletext.hasError() else enablError.toBoolean()

               TextField(
                   value = mobiletext.value,
                   isError = error_display,
                   singleLine = true,
                   onValueChange = { mobiletext.value = it },
                   label = { Text("Mobile number") },
                   placeholder = { Text("080555555555") },
                   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                   modifier = Modifier
                       .fillMaxWidth(1f)

               )

               AnimatedVisibility(visible = error_display) {

                   Text(
                       text = mobiletext.errorMessage ?: "",
                       modifier = Modifier.fillMaxWidth(),
                       style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
                   )

               }

           }

       }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {

            val dialogState = rememberMaterialDialogState()
            val cal = Calendar.getInstance()

            SelectableTextField(modifier = Modifier
                .weight(1f)
                .fillMaxWidth(1f)
                .padding(end = 10.dp),
                onValueChange = {

                },
                label = "Birthdate",
                textValue = birthdate.value,
                onClick = {


                    val dpd = DatePickerDialog(
                        context,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            // Display Selected date in TextView
                            birthdate.value =
                                "${year}-${month + 1}-${dayOfMonth}"
                        },
                        1999,
                        month,
                        day
                    )
                    dpd.show()


                })

                var expanded_dep by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded_dep,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(1f),
                    onExpandedChange = {
                        expanded_dep = !expanded_dep
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = departmentsOptionText,
                        onValueChange = { },
                        singleLine = true,
                        label = {
                            Text(
                                "Faculties and Departments",
                                style = TextStyle(fontSize = 17.sp)
                            )
                        },
                        textStyle = TextStyle(fontSize = 17.sp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded_dep
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(1f),
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded_dep,
                        onDismissRequest = {
                            expanded_dep = false
                        }
                    ) {
                        departmentsOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    departmentsOptionText =
                                        selectionOption
                                    expanded_dep = false
                                }
                            ) {
                                Text(text = selectionOption)
                            }
                        }
                    }
                }



        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween)
        {
            var expanded_work by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded_work,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                onExpandedChange = {
                    expanded_work = !expanded_work
                }
            ) {
                TextField(
                    readOnly = true,
                    singleLine = true,
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
                            expanded = expanded_work
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded_work,
                    onDismissRequest = {
                        expanded_work = false
                    }
                ) {
                    positionOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                workPositionOptionText =
                                    selectionOption
                                expanded_work = false
                            }
                        ) {
                            Text(text = selectionOption)
                        }
                    }
                }
            }

            TextField(
                value = uniqueNotext,
                onValueChange = { uniqueNotext = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                ),
                label = { Text("Unique ID no") },
                placeholder = { Text("BAS-555555555") },
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(1f)

            )

        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {

            var expandedGender by remember { mutableStateOf(false) }
            var expandedCat by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedGender,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                onExpandedChange = {
                    expandedGender = !expandedGender
                }
            ) {

                TextField(
                    readOnly = true,
                    value = genderOptionText,
                    onValueChange = { },
                    singleLine = true,
                    label = {
                        Text(
                            "Gender",
                            style = TextStyle(fontSize = 17.sp)
                        )
                    },
                    textStyle = TextStyle(fontSize = 17.sp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedGender
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = {
                        expandedGender = false
                    }
                ) {
                    genderOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                genderOptionText = selectionOption
                                expandedGender = false
                            }
                        ) {
                            Text(text = selectionOption)
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedCat,
                modifier = Modifier.weight(1f),
                onExpandedChange = {
                    expandedCat = !expandedCat
                }
            ) {
                TextField(
                    readOnly = true,
                    singleLine = true,
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
                            expanded = expandedCat
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedCat,
                    onDismissRequest = {
                        expandedCat = false
                    }
                ) {
                    categoryOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                categoryOptionText = selectionOption
                                expandedCat = false
                            }
                        ) {
                            Text(text = selectionOption)
                        }
                    }
                }
            }

        }
    }

    functionVariable(FormData(
        textFullname,
        emailField,
        mobiletext,
        genderOptionText,
        categoryOptionText,
        workPositionOptionText,
        birthdate.value,
        departmentsOptionText,
        uniqueNotext,
    ))

}