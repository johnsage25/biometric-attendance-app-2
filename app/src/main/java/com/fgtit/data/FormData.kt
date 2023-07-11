package com.fgtit.data

import androidx.compose.ui.text.input.TextFieldValue
import tech.devscast.validable.EmailValidable
import tech.devscast.validable.NotEmptyValidable

data class FormData(
    var textFullname: NotEmptyValidable,
    var emailField: EmailValidable,
    var mobiletext: NotEmptyValidable,
    var genderOptionText: String,
    var categoryOptionText: String,
    var workPositionOptionText: String,
    var birthdate: String,
    var departmentsOptionText: String,
    var uniqueNotext: String,
)