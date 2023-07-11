package com.pearldrift.handsets.ui.nav

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.parse.ParseUser
import com.pearldrift.handsets.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar (title: String, context: Context, onClick: () -> Unit){

    var expanded = remember {
        mutableStateOf(false)
    }

    SmallTopAppBar(
        title = { Text(title, style = TextStyle(color = Color.White, fontSize = 18.sp) )},
        colors =  TopAppBarDefaults.mediumTopAppBarColors(containerColor = colorResource(id = R.color.blue_500)),
        navigationIcon = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    tint = Color.White,
                    contentDescription = "Home"
                )
            }
        },

        actions = {
            Column() {
                Box(modifier = Modifier
                    .width(50.dp)
                    .align(Alignment.CenterHorizontally)) {
                    IconButton(onClick = { expanded.value = true }) {
                        Icon(Icons.Default.MoreVert, tint = Color.White, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        modifier = Modifier.width(180.dp),
                        onDismissRequest = { expanded.value = false }
                    ) {

                        DropdownMenuItem(

                            content = {
                                Icon(Icons.Default.Settings, contentDescription = "settings")
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Settings")
                            },
                            onClick = { /* Handle settings! */ },
                        )
                        DropdownMenuItem(
                            content = {
                                Icon(painter = painterResource(id = R.drawable.ic_logout), modifier = Modifier.width(25.dp),  contentDescription = "logout")
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Logout")
                            },

                            onClick = onClick,
                            )
                    }
                }
            }

        },
        modifier = Modifier.shadow(elevation = 3.dp),
        scrollBehavior = null,
    )

}


@Composable

fun TopBarWithBack (title: String, backpress: () -> Unit ) {

    var backPressedCount  = remember { mutableStateOf(0) }
    val context = LocalContext.current
    val navController = rememberNavController()


    SmallTopAppBar(
        title = { Text(title, style = TextStyle(color = Color.White, fontSize = 20.sp) )},
        colors =  TopAppBarDefaults.mediumTopAppBarColors(containerColor = colorResource(id = R.color.blue_500)),
        navigationIcon = {
            IconButton(onClick = backpress) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Localized description"
                )
            }
        },

        modifier = Modifier.shadow(elevation = 3.dp),
    )
}