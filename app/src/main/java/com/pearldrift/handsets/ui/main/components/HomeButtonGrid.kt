package com.pearldrift.handsets.ui.main.components

import android.util.DisplayMetrics
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearldrift.handsets.R


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeButtonGrid(onClick: (item: homeList) -> Unit){
    val list = (1..6).map { it.toString() }
    var homelist =  mutableListOf<homeList>()
    homelist.add(homeList("sign_in", "Sign In", R.drawable.ic_check_in, "Sign In"))
    homelist.add(homeList("sign_out", "Sign Out", R.drawable.ic_check_out, "Sign Out"))
    homelist.add(homeList("register_staff", "Enrol Staff", R.drawable.ic_add_friend, "Enrol Staff"))
    homelist.add(homeList("staff_list", "Enroll List", R.drawable.ic_list, "Enroll List"))
    homelist.add(homeList("verify_id", "Verify ID", R.drawable.ic_verified, "Verify ID"))
    homelist.add(homeList("setting_id", "Settings", R.drawable.ic_settings__2, "Settings"))

   var interactionSource = remember { MutableInteractionSource() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),

        contentPadding = PaddingValues(
            start = 9.dp,
            top = 10.dp,
            end = 9.dp,
            bottom = 10.dp
        ),
        modifier = Modifier
            .fillMaxHeight(1f)
            .padding(top = 20.dp)

    ) {
        items(homelist.size) { index ->
            Card(
                backgroundColor = Color.White,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(180.dp),
                elevation = 2.dp,
            ) {
                var item = homelist[index]
                Box(modifier = Modifier
                    .fillMaxWidth(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true), // You can also change the color and radius of the ripple
                        onClick = { onClick(item) }
                    )
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Image(painter = painterResource(id = item.icon), modifier = Modifier.size(width = 65.dp, height = 65.dp), contentDescription =item.description )
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Normal,
                            fontSize = 27.sp,
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}