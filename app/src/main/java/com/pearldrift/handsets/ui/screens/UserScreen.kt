package com.pearldrift.handsets.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.datetimeutils.DateTimeStyle
import com.datetimeutils.DateTimeUtils
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.Attendance
import com.fgtit.model.Attendance_
import com.fgtit.model.EnrolmentModel
import com.pearldrift.handsets.R
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.skydoves.landscapist.glide.GlideImage
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter


class UserScreen(userid: Long) : Screen {
    var id = userid

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        var navigator = LocalNavigator.currentOrThrow
        val enrolBox = ObjectBox.store.boxFor(EnrolmentModel::class.java)
        var userDetails = enrolBox.get(id)
        Surface(modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(1f)) {
            Scaffold(
                topBar = {
                    TopBarWithBack(title = "User Details"){
                        navigator.pop()
                    }
                },
                bottomBar = {

                }
            ){
                Column() {


                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp), elevation = 4.dp
                    ) {
                        Row( horizontalArrangement = Arrangement.SpaceBetween ,verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {

                            userDetails.let {

                                Box(modifier = Modifier
                                    .weight(0.3f)) {
                                    Box(Modifier.fillMaxWidth()) {
                                        GlideImage(
                                            imageModel = "${it.image}",
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
                                }
                                Box(modifier = Modifier
                                    .weight(1f), contentAlignment = Alignment.TopStart){
                                    Column() {
                                        Row() {
                                            Text(text = "${it.fullname}", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            if (it?.enrolled_date.toString().isNotEmpty()){
                                                Chip(onClick = { /*TODO*/ }) {
                                                    val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                                                    val date = LocalDateTime.parse(it?.enrolled_date.toString(), firstApiFormat).toLocalDate().format(                                  // Generate text representing the value of that `LocalDate` object.
//                                                        DateTimeFormatter
//                                                            .ofLocalizedDate( FormatStyle.FULL )
//                                                            .withLocale( Locale.US )
//                                                    )

                                                    Text(text = "${it?.enrolled_date.toString()}")
                                                }
                                            }

                                        }


                                        Spacer(modifier = Modifier.height(10.dp))
                                        Box(modifier = Modifier.fillMaxWidth(1f)){
                                            Column() {
                                                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                                                    Row() {
                                                        Text(text = "Position:", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(text = "${it.work_position.toString()}", fontSize = 17.sp)
                                                    }

                                                    Spacer(modifier = Modifier.width(15.dp))

                                                    Row() {
                                                        Text(text = "Gender:", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(text = "${it.gender.toString()}", fontSize = 17.sp)
                                                    }

                                                    Spacer(modifier = Modifier.width(15.dp))

                                                    Row() {
                                                        Text(text = "Mobile:", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(text = "${it.mobile.toString()}", fontSize = 17.sp)
                                                    }

                                                }

                                                Divider(modifier = Modifier.fillMaxWidth())

                                                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                                                    Row() {
                                                        Text(text = "Category:", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(text = "${it.staff_category.toString()}", fontSize = 17.sp)
                                                    }
                                                    Spacer(modifier = Modifier.width(15.dp))
                                                    Row() {
                                                        Text(text = "Department:", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                        Text(text = "${it.fac_a_department.toString()}", fontSize = 17.sp)
                                                    }

                                                }
                                            }
                                            Box() {

                                            }
                                        }
                                    }

                                }

                            }

                        }
                    }
                    // List display


                    val attendanceBox =
                        ObjectBox.store.boxFor(Attendance::class.java)

                    var allAttendance =
                        attendanceBox?.query(Attendance_.staff_uuid.equal(userDetails.uuid))?.build()?.find()
                    if(allAttendance?.size?.equals(0) == true){

                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .fillMaxWidth(), contentAlignment = Alignment.Center) {

                            Column(modifier = Modifier
                                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter = painterResource(id = R.drawable.ic_history__1), contentDescription = "history",
                                    modifier = Modifier.size(height = 200.dp, width = 200.dp))
                                Text(text="No attendance", fontSize = 25.sp, color = colorResource(id = R.color.transparent_db))
                            }
                        }
                    }
                    else{

                        LazyColumn(modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)) {
                            allAttendance?.let { it1 ->
                                items(it1?.size, key = {
                                    allAttendance[it].id
                                }, itemContent = { index ->
                                    var items = allAttendance.get(index)
                                    var image = if (items.attns_type.equals("signed_in")) R.drawable.ic_check_in else R.drawable.ic_check_out
                                    items.let {

                                        ListItem(
                                            icon = {

                                                GlideImage(
                                                    imageModel = image,
                                                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                    Modifier
                                                        .width(50.dp)
                                                        .height(50.dp)
                                                        .padding(5.dp),
                                                    requestOptions = {
                                                        RequestOptions()
                                                            .override(50, 50)
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .centerCrop()
                                                    },
                                                    contentScale = ContentScale.Crop,
                                                    // shows a placeholder while loading the image.
                                                    placeHolder = ImageBitmap.imageResource(R.drawable.avatar),
                                                    // shows an error ImageBitmap when the request failed.
                                                    error = ImageBitmap.imageResource(R.drawable.avatar)
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 15.dp)
                                                .height(70.dp),
                                            text = {
                                                Column() {
                                                    var text =
                                                        if (it?.attns_type.equals("signed_in")) "SIGNED IN" else "SIGNED OUT"
                                                    androidx.compose.material.Text(
                                                        text = text,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.padding(bottom = 5.dp))
                                                    Row() {
                                                        androidx.compose.material.Text(text = "Entrance type: ")
                                                        androidx.compose.material.Text(text = "${items.capture_type?.toUpperCase()}")
                                                    }

                                                }

                                            },
                                            trailing = {

//

                                                Log.d("user", "Date ${items.timestamp_date}")

                                                var mmddyyyy1 = ""
                                                if(items.timestamp_date !==null ){
                                                    mmddyyyy1 = DateTimeUtils.formatWithStyle(items.timestamp_date,
                                                        DateTimeStyle.MEDIUM)

                                                }


                                                var time = ""
                                                if(items.time!!.isNotEmpty()){

                                                    val sdf = SimpleDateFormat("H:mm")
                                                    val dateObj = sdf.parse(items.time)

                                                    time = SimpleDateFormat("K:mm a").format(dateObj)


                                                }



                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    androidx.compose.material.Text(text = time,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold)
                                                    androidx.compose.material.Text(text = mmddyyyy1,
                                                        fontSize = 15.sp)
                                                }

                                            },

                                            )

                                        Divider(modifier = Modifier.fillMaxWidth(), color = colorResource(
                                            id = R.color.gray
                                        ))

                                    }
                                }
                                )
                            }
                        }

                    }

                }

            }
        }
    }

}