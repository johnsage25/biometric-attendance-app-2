package com.pearldrift.handsets.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.EnrolmentModel
import com.fgtit.model.EnrolmentModel_
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pearldrift.handsets.R
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.skydoves.landscapist.glide.GlideImage
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import kotlinx.android.parcel.Parcelize


@Parcelize
object EnrolledList : Screen {

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
        ExperimentalAnimationApi::class
    )
    @Composable
    override fun Content(){

        var context = LocalContext.current
        var navigator = LocalNavigator.currentOrThrow
        var state = rememberSwipeRefreshState(isRefreshing = false)

        val textSearchState = remember { mutableStateOf(TextFieldValue("")) }

        var rememberState = remember { mutableListOf(0)
        }





        var interactionSource = remember {
            MutableInteractionSource()
        }

        var enrolBox = ObjectBox.store.boxFor(EnrolmentModel::class.java)
        var enrolList = enrolBox.query().contains(
            EnrolmentModel_.fullname, textSearchState.value.text, QueryBuilder.StringOrder.CASE_INSENSITIVE).order(EnrolmentModel_.uuid)

        val query: Query<EnrolmentModel> = enrolList.build()

        val list :List<EnrolmentModel> = query.find()


        var statehideHint by rememberSaveable { mutableStateOf(true) }

            BASHandsetsCompatiblektTheme() {
                Surface(modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(1f)) {
                    Scaffold(
                        topBar = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                TopBarWithBack(title = "Enroll List (${list.size})"){
                                    navigator.pop()
                                }
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                    elevation = 3.dp
                                ) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {

                                        Box(modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .fillMaxSize(), contentAlignment = Alignment.CenterStart) {

                                            BasicTextField(
                                                value = textSearchState.value,
                                                singleLine = true,
                                                interactionSource =  interactionSource,
                                                onValueChange = {
                                                    textSearchState.value = it
                                                    if(textSearchState.value.text.isNotEmpty() && textSearchState.value.text.isNotBlank()){
                                                        statehideHint = false
                                                    }
                                                    else{
                                                        statehideHint = true
                                                    }

                                                },
                                                textStyle = TextStyle(fontSize = 19.sp),
                                                modifier = Modifier
                                                    .fillMaxWidth(1f)
                                                    .fillMaxHeight(4f)
                                                    .padding(vertical = 10.dp, horizontal = 5.dp)
                                                    .background(color = Color.White)
                                                    .height(46.dp),
                                            )
                                            Column() {
                                                AnimatedVisibility(visible = statehideHint,
                                                    enter = fadeIn(), exit = fadeOut()) {
                                                    Text(text = "Search.....", fontSize = 20.sp, color = Color.LightGray)
                                                }
                                            }



                                        }
                                        Box(modifier = Modifier.width(45.dp), contentAlignment = Alignment.CenterStart) {
                                            IconButton(onClick = {

                                            }, modifier = Modifier.width(45.dp)) {
                                                Icon(painter = painterResource(id = R.drawable.ic_filter_list_fill0), contentDescription = "")
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        content = ({
                            Box() {



                                SwipeRefresh(
                                    state = state,
                                    onRefresh = {
                                          Log.d("user", "swipe")
                                    },
                                ) {
                                    val scrollState = rememberScrollState()
                                    LazyColumn(modifier = Modifier.fillMaxHeight().scrollable(
                                        orientation = Orientation.Vertical,
                                        interactionSource = null,
                                        state = scrollState
                                    )) {

                                        items(list.size, key = {
                                            list[it].id
                                        }, itemContent = { item ->
                                            var itms = list.get(item)
                                            Log.d("user", "nfc_code: ${itms.nfc_card_code}")
                                            var deleted = remember{
                                                mutableStateOf(true)
                                            }
                                            val density = LocalDensity.current
                                            AnimatedVisibility(visible = deleted?.value,
                                                enter = slideInVertically {
                                                    // Slide in from 40 dp from the top.
                                                    with(density) { -40.dp.roundToPx() }
                                                } + expandVertically(
                                                    // Expand from the top.
                                                    expandFrom = Alignment.Top
                                                ) + fadeIn(
                                                    // Fade in with the initial alpha of 0.3f.
                                                    initialAlpha = 0.3f
                                                ),
                                                exit = slideOutVertically() + shrinkVertically() + fadeOut()

                                            ) {

                                                //list

                                                ListItem(
                                                    icon = {
                                                        GlideImage(
                                                            imageModel = itms.image,
                                                            // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                            Modifier
                                                                .width(70.dp)
                                                                .height(70.dp)
                                                                .clip(RoundedCornerShape(50)),
                                                            requestOptions = {
                                                                RequestOptions()
                                                                    .override(70, 70)
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
                                                        .align(Alignment.Center)

                                                        .combinedClickable(
                                                            onClick = {
                                                                navigator.push(
                                                                    UserScreen(userid = itms.id)
                                                                )
                                                            },
                                                            onLongClick = {


                                                                Toast
                                                                    .makeText(
                                                                        context,
                                                                        "Long Press",
                                                                        Toast.LENGTH_SHORT
                                                                    )
                                                                    .show()
                                                            },
                                                            interactionSource = MutableInteractionSource(),
                                                            indication = rememberRipple(bounded = true)
                                                        ),
                                                    text = {
                                                        Text(
                                                            text = itms.fullname.toString(),
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    },
                                                    secondaryText = {
                                                        Row(modifier = Modifier.padding(vertical = 10.dp)) {
                                                            Row() {
                                                                androidx.compose.material3.Text(
                                                                    text = "Gender:",
                                                                    fontSize = 17.sp,
                                                                    fontWeight = FontWeight.SemiBold
                                                                )
                                                                Spacer(modifier = Modifier.width(5.dp))
                                                                Text(
                                                                    text = "${itms.gender.toString()}",
                                                                    fontSize = 17.sp
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(15.dp))
                                                            Row() {
                                                                androidx.compose.material3.Text(
                                                                    text = "Position:",
                                                                    fontSize = 17.sp,
                                                                    fontWeight = FontWeight.SemiBold
                                                                )
                                                                Spacer(modifier = Modifier.width(5.dp))
                                                                Text(
                                                                    text = "${itms.work_position.toString()}",
                                                                    fontSize = 17.sp
                                                                )
                                                            }

                                                        }
                                                    },
                                                    singleLineSecondaryText = true,
                                                    overlineText = {
                                                        Text(text = "")
                                                    },
                                                    trailing = {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxHeight(),
                                                            verticalArrangement = Arrangement.Center
                                                        ) {

                                                            val openDialog = remember { mutableStateOf(false) }

                                                            if (openDialog.value) {
                                                                AlertDialog(
                                                                    onDismissRequest = {
                                                                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                                                                        // button. If you want to disable that functionality, simply use an empty
                                                                        // onCloseRequest.
                                                                        openDialog.value = false
                                                                    },
                                                                    title = {
                                                                        Text(text = "Are you sure?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                                                    },
                                                                    text = {
                                                                        Text(
                                                                            "Note: This user will not be visible only on this device. to completely delete user, you must contact SuperAdmin.",
                                                                            fontSize = 17.sp
                                                                        )
                                                                    },
                                                                    confirmButton = {
                                                                        TextButton(
                                                                            onClick = {
                                                                                deleted.value = false
                                                                                enrolBox.remove(itms.id)
                                                                                openDialog.value = false
                                                                            }
                                                                        ) {
                                                                            Text("Confirm")
                                                                        }
                                                                    },
                                                                    dismissButton = {
                                                                        TextButton(
                                                                            onClick = {
                                                                                openDialog.value = false
                                                                            }
                                                                        ) {
                                                                            Text("Dismiss")
                                                                        }
                                                                    }
                                                                )
                                                            }

                                                            IconButton(onClick = {


                                                                openDialog.value = true;
//
                                                            }) {
                                                                Image(
                                                                    painter = painterResource(id = R.drawable.ic_delete),
                                                                    modifier = Modifier.width(30.dp),
                                                                    contentDescription = "enrol delete"
                                                                )
                                                            }


                                                        }


                                                    },
                                                )
                                                Divider(modifier = Modifier.fillMaxWidth(), color = colorResource(
                                                    id = R.color.gray
                                                ))
                                            }



                                        })
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