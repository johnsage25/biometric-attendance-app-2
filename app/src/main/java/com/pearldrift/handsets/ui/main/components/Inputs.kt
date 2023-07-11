package com.pearldrift.handsets.ui.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


@Composable
fun SelectableTextField(
    modifier: Modifier = Modifier,
    textValue: String,
    label: String,
    onValueChange: () -> Unit,
    onClick: ()->Unit,
) {

    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) {
        onClick()
    }


       TextField(
           value = textValue,
           readOnly = true,
           onValueChange = { onValueChange },
           label = { Text(label, style = TextStyle(fontSize = 17.sp)) },
           textStyle = TextStyle(fontSize = 17.sp),
           singleLine = true,
           modifier = modifier,
           interactionSource = source
       )

}
