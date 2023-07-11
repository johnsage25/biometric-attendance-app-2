package com.pearldrift.handsets.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.Attendance
import com.github.kittinunf.fuel.Fuel
import com.parse.ParseUser
import com.pearldrift.handsets.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PeriodicTimeWorker(context: Context, val workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {


        Toast.makeText(applicationContext, "Background upload started.", Toast.LENGTH_SHORT)
            .show()


        val aData = workerParams.inputData

        Log.e("user-log ", "in task")

        val attendance = ObjectBox.store.boxFor(Attendance::class.java)
        var atten = attendance.query().build().find()


        GlobalScope.launch(Dispatchers.IO) {

            var adminStaff = ParseUser.getCurrentUser()

            try {
                for ((key, item) in atten.withIndex()) {

                    Fuel.post("${applicationContext?.getString(R.string.basApi)}/api/attendance",
                        listOf(
                            "staff_objectId" to item.staff_objectId,
                            "staff_uuid" to item?.staff_uuid.toString(),
                            "uid" to item?.uid.toString(),
                            "enrol_id" to item?.id.toString(),
                            "timestamp_date" to item.timestamp_date,
                            "capture_type" to item?.capture_type.toString(),
                            "location" to item?.location.toString(),
                            "timestamp" to item.timestamp,
                            "time" to item?.time.toString(),
                            "attns_type" to item.attns_type,
                            "device_admin" to adminStaff.objectId
                        )
                    )
                        .also {

                        }
                    Log.d("user-log", "${key}")
                    if (key >= atten.size) {

                    }

                }
            } catch (e: Exception) {
                Log.d("user-log", "${e.message}")
            }
        }


        val adata=createOutputData()


        return Result.success(adata)
    }

    private fun createOutputData(): Data {
        return Data.Builder().putString("Success", "1").build()
    }
}