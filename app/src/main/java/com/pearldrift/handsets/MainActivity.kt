package com.pearldrift.handsets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.work.*
import androidx.work.OneTimeWorkRequest.from
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.Attendance
import com.github.kittinunf.fuel.Fuel
import com.parse.ParseUser
import com.pearldrift.handsets.service.PeriodicTimeWorker
import com.pearldrift.handsets.ui.screens.HomeScreen
import com.pearldrift.handsets.util.ExtApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    var receiver: BroadcastReceiver? = null
    lateinit var  mPeroidRequest: PeriodicWorkRequest.Builder
    lateinit var aPeriodicWork: PeriodicWorkRequest

    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
        ExperimentalAnimationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            Navigator(screen = HomeScreen()) { navigator ->
                FadeTransition(navigator)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }





}