package com.pearldrift.handsets

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.fgtit.fpcore.ObjectBox
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import com.parse.Parse
import com.pearldrift.handsets.service.NetworkChanges
import com.pearldrift.handsets.service.PeriodicTimeWorker
import okhttp3.Cache.Companion.key
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.experimental.and

class MyApplication : Application() {
    lateinit var  mPeroidRequest: PeriodicWorkRequest.Builder
    lateinit var aPeriodicWork: PeriodicWorkRequest
    lateinit var kronosClock: KronosClock

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)

//        mPeroidRequest= PeriodicWorkRequest.Builder(PeriodicTimeWorker::class.java,15, TimeUnit.MINUTES)
//            .setConstraints(setConstraint())
//        aPeriodicWork=mPeroidRequest.build()
////
////
//        WorkManager
//            .getInstance(this)
//            .enqueue(aPeriodicWork)

        setAlarm()


        try {

           kronosClock = AndroidClockFactory.createKronosClock(this)
           kronosClock.syncInBackground()

           Parse.enableLocalDatastore(this);
           Parse.initialize(
               Parse.Configuration.Builder(this)
                   .applicationId(getString(R.string.app_id)) // if defined
                   .clientKey(getString(R.string.key))
                   .server(getString(R.string.server_ip))
                   .build()
           )
       }catch (e: Exception){
           Toast.makeText(this, "Server out of reach 440", Toast.LENGTH_SHORT).show()
       }
    }

    private fun setConstraint(): Constraints {

        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // check internet connectivity
            .setRequiresBatteryNotLow(true) // check battery level
            //.setRequiresCharging(true) // check charging mode
            // .setRequiresStorageNotLow(true) // check storage
            // .setRequiresDeviceIdle(false) // check device idle state
            .build()

    }

    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NetworkChanges::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent
        )
    }


}