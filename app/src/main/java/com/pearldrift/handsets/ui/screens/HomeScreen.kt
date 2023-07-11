package com.pearldrift.handsets.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fgtit.fpcore.StaffAuthClass
import com.parse.ParseFile
import com.parse.ParseUser
import com.pearldrift.handsets.*
import com.pearldrift.handsets.R
import com.pearldrift.handsets.ui.main.components.HomeButtonGrid
import com.pearldrift.handsets.ui.nav.TopAppBar
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.skydoves.landscapist.glide.GlideImage


class HomeScreen : Screen {

    lateinit var file: ParseFile;

    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {



        BASHandsetsCompatiblektTheme{
            Surface(
                color = colorResource(id = R.color.gray),
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f)
            ) {
                val context = LocalContext.current
                val userid = StaffAuthClass().readFromSharedPreferences("userId", context)

                val staff =  StaffAuthClass().getStaffDetailBox(userid)

                Scaffold(
                    containerColor = colorResource(id = R.color.gray),
                    topBar = {
                    TopAppBar(title = "Home", context, {
                        ParseUser.logOut();
                        val settings = context.getSharedPreferences("PreferencesName",
                            Context.MODE_PRIVATE)
                        settings.edit().clear().commit()
                        val activity = (context as? Activity)
                        var intent = Intent(activity, LoginActivity::class.java)
                        context?.startActivity(intent)
                        activity?.finish()
                    })
                }, content = {
                    Column() {


                        Card(shape = RectangleShape,  modifier = Modifier
                            .drawBehind {
                                val y = size.height - 1 / 2
                                drawLine(
                                    Color.LightGray,
                                    Offset(0f, y),
                                    Offset(size.width, y),
                                    1f
                                )
                            }
                            .fillMaxWidth(1f)
                        ) {


                            Box(modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 30.dp)
                                .fillMaxWidth(1f)
                            ) {

                                Row() {

                                    Box(modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()) {


                                        Row( horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically ) {



                                            Box() {
                                                GlideImage(
                                                    imageModel = staff[0].staffAvatar,
                                                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                    Modifier
                                                        .width(120.dp)
                                                        .height(120.dp)
                                                        .clip(RoundedCornerShape(50)),
                                                    requestOptions = {
                                                        RequestOptions()
                                                            .override(120, 120)
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .centerCrop()
                                                    },
                                                    contentScale = ContentScale.Crop,
                                                    // shows a placeholder while loading the image.
                                                    placeHolder = ImageBitmap.imageResource(R.drawable.avatar),
                                                    // shows an error ImageBitmap when the request failed.
                                                    error = ImageBitmap.imageResource(R.drawable.avatar)
                                                )
                                            }
                                            Spacer(modifier = Modifier
                                                .width(10.dp)
                                                .padding(vertical = 30.dp))
                                            Box(modifier = Modifier.fillMaxWidth(1f)){
                                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Box() {
                                                        Column() {
                                                            Text(text = "${staff[0].fullname}", textAlign = TextAlign.Center, style = TextStyle(color = Color.Black, fontSize = 30.sp, fontWeight = FontWeight.Bold))
                                                            Box(modifier = Modifier.padding(vertical = 5.dp)){
                                                                Chip(onClick = { /*TODO*/ }) {
                                                                    Text(text = "${staff[0].email}")
                                                                }
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }

                                    Box(modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .height(100.dp)) {
                                        Row(
                                            Modifier
                                                .fillMaxWidth(1f)
                                                .height(100.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {

//                                            IconButton(onClick = {
//                                                val intent = Intent(context, AccountActivity::class.java)
//                                                context.startActivity(intent)
//                                            }) {
//                                                Image(painter = painterResource(id = R.drawable.ic_edit__2_), modifier = Modifier.width(45.dp) , contentDescription = "attendace download")
//                                            }
//
                                            Spacer(modifier = Modifier
                                                .width(10.dp)
                                                .padding(vertical = 30.dp))

                                            IconButton(onClick = {
                                                context.startActivity(Intent(context, SyncActivity::class.java))
                                            }) {
                                                Image(painter = painterResource(id = R.drawable.ic_synchronize), modifier = Modifier.width(45.dp) , contentDescription = "attendace download")
                                            }
//
                                        }
                                    }
                                }


                            }
                        }



                        var navigator = LocalNavigator.currentOrThrow

                        Box(modifier = Modifier.weight(4f, true)) {
                            HomeButtonGrid(){


                                when(it.action){
                                    "sign_in" -> {
                                        val intent = Intent(context, SignInActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    "sign_out" -> {
                                        val intent = Intent(context, SignOutActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    "register_staff" -> {
                                        val intent = Intent(context, EnrolmentActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    "staff_list" -> {
                                        navigator.push(EnrolledList)
                                    }
                                    "verify_id" ->{
                                        val intent = Intent(context, VerifyActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    "setting_id" -> {

                                    }
                                }

                            }
                        }


                    }
                }, bottomBar = {
                    Box(modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp)
                        .fillMaxWidth(1f), contentAlignment = Alignment.Center ) {
                        Column(modifier = Modifier
                            .fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Designed by PearlDrift Technologies LTD", fontStyle = FontStyle.Normal,  textAlign = TextAlign.Center)
                            Text(text = "Version 1.0.0", fontStyle = FontStyle.Normal,  textAlign = TextAlign.Center)
                        }
                    }
                })


            }
        }
    }
}