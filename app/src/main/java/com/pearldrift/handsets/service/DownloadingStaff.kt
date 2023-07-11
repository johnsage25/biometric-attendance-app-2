package com.pearldrift.handsets.service

import android.app.IntentService
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.fgtit.data.StaffArrayUUID
import com.fgtit.data.StaffDataDo
import com.fgtit.data.StaffX
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.EnrolmentModel
import com.google.gson.Gson
import com.parse.ParseUser
import com.pearldrift.handsets.util.ExtApi

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.pearldrift.handsets.service.action.FOO"
private const val ACTION_BAZ = "com.pearldrift.handsets.service.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.pearldrift.handsets.service.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.pearldrift.handsets.service.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class DownloadingStaff : IntentService("DownloadingStaff") {

    override fun onHandleIntent(intent: Intent?) {
        Log.d("user-log", " service running")
        handleStaffSync()
    }

    private fun handleStaffSync () {

        val queue = Volley.newRequestQueue(applicationContext)

        val sendProgress = Intent()

        val sr: StringRequest = object : StringRequest(Method.GET, "http://192.168.0.106:3000/api/getstaffs",
            Response.Listener { response ->


                var staffs = Gson().fromJson<StaffArrayUUID>(
                    response,
                    StaffArrayUUID::class.java)
                var counter = 0;

                for ((key, it) in staffs.staff.withIndex()){


                    var st = sendRequest(it, key)

                    Log.d("user-log", "${it.objectId}")

                    if(st.toInt() > 0){
                        sendProgress.action = "GET_DOWNLOAD_COUNTER"
                        sendProgress.putExtra("progress",key)
                        sendBroadcast(sendProgress)
                    }

                }



            },
            Response.ErrorListener { error ->
                //your error
            }) {

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }
        queue.add(sr)

    }

    fun sendRequest(it: StaffX, key: Number): Number{

        val queue = Volley.newRequestQueue(applicationContext)

        val sr: StringRequest = object : StringRequest(Method.POST, "http://192.168.0.106:3000/api/getstaff",
            Response.Listener { response ->

                val staff = Gson().fromJson(response, StaffDataDo::class.java)

                try {



                    if(!staff.uuid.isEmpty()){
                    var imageData = ExtApi.uriToImage(staff.staff_image.url)
                        var bitmap = ExtApi.saveImage(Glide.with(applicationContext)
                            .asBitmap()
                            .load("${staff.staff_image.url}") // sample image
                            .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                            .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                            .submit()
                            .get(),
                            staff.staff_image.name,
                            applicationContext
                        )

                        val enrolBox =
                            ObjectBox.store.boxFor(EnrolmentModel::class.java)

                        var formData = EnrolmentModel(
                            uuid = staff.uuid,
                            fullname = staff.fullname,
                            birthdate = ExtApi.StrToDateFormat(staff.birthdate.toString()),
                            fac_a_department = staff.fac_a_department,
                            work_position = staff.work_position,
                            staff_category = staff.staff_category,
                            gender = staff.gender,
                            unique_id_no = staff.unique_id_no,
                            username = staff.username,
                            password = "",
                            email = staff.email,
                            mobile = staff.mobile,
                            left_fingerprint = staff.left_fingerprint?.trim(),
                            left_inpsize = 256,
                            right_inpsize = 256,
                            right_fingerprint = staff.right_fingerprint?.trim(),
                            nfc_card_code = staff.nfc_card_code,
                            enrolled_date = ExtApi.StrToDateFormat(staff.enrolled_date.iso.toString()),
                            image = "${applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM)!!}/${staff.staff_image.name}",
                            by_staff = ParseUser.getCurrentUser().objectId,
                            objectId = staff.objectId,
                            work_hour = staff?.work_hour,
                        )

                        enrolBox.put(formData)

                    }

                }
                catch (e: Exception){
                    Log.e("user-log", "${e}")
                }

            },
            Response.ErrorListener { error ->
                Log.e("user-log", "${error}")
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["objectId"] =  "${it.objectId}"
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }

        queue.add(sr)

        return key
    }

}