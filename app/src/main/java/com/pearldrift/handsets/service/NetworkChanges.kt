package com.pearldrift.handsets.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.Attendance
import com.fgtit.model.Attendance_
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.parse.ParseUser
import com.pearldrift.handsets.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NetworkChanges : BroadcastReceiver() {

    override fun onReceive(p0: Context, p1: Intent?) {

        val attendance = ObjectBox.store.boxFor(Attendance::class.java)

        var attenSI = attendance.query(Attendance_.attns_type.equal("signed_in")).build().find()
        var attenSO = attendance.query(Attendance_.attns_type.equal("signed_out")).build().find()


        if(isConnectingToInternet(p0)) {

            Toast.makeText(p0, "Starting background upload.", Toast.LENGTH_SHORT).show()

            GlobalScope.launch(Dispatchers.IO) {

                var adminStaff = ParseUser.getCurrentUser()

                try {
                    for ((key, item) in attenSI.withIndex()) {

                        var json = Gson().toJson(item)

                        Fuel.post("${p0?.getString(R.string.basApi)}/api/attendance").jsonBody(json).also {
                            var callback = it.awaitString()
                            Log.d("user-log", "${callback}")
                        }

                    }

                    for ((key, item) in attenSO.withIndex()) {

                        var json = Gson().toJson(item)

                        Fuel.post("${p0?.getString(R.string.basApi)}/api/attendance").jsonBody(json).also {
                            var callback = it.awaitString()
                            Log.d("user-log", "${callback}")
                        }

                    }

                } catch (e: Exception) {
                    Log.d("user-log", "${e.message}")
                }
            }

        }
        else{
            Toast.makeText(p0, "Connection lost", Toast.LENGTH_SHORT).show()
        }
    }

    fun isConnectingToInternet(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}