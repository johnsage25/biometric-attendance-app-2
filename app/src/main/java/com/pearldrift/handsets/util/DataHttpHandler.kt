package com.pearldrift.handsets.util

import android.content.Context
import android.os.Environment
import android.provider.Settings
import android.util.Log
import com.datetimeutils.DateTimeUtils
import com.fgtit.data.StaffXX
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.EnrolmentModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.pearldrift.handsets.R
import io.objectbox.Box
import okhttp3.internal.and
import java.io.File
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class DataHttpHandler {

    fun sendStaffData (context: Context, filename: String?, staffData: EnrolmentModel){

        val postBody =   with(staffData) {

           listOf(
                "by_staff" to by_staff,
                "birthdate" to birthdate,
               "fullname" to fullname,
                "username" to username,
                "fac_a_department" to fac_a_department,
                "work_position" to work_position,
                "staff_category" to staff_category,
                "gender" to gender,
                "unique_id_no" to unique_id_no,
                "mobile" to mobile,
                "email" to email,
                "uuid" to uuid,
                "enrolled_date" to enrolled_date,
                "password" to password,
                "nfc_card_code" to nfc_card_code,
                "work_hour" to "",
                "image" to image,
                "left_fingerprint" to left_fingerprint,
                "left_inpsize" to left_inpsize,
                "right_fingerprint" to right_fingerprint,
                "right_inpsize" to right_inpsize,

            )

        }


        val imageFile = File(context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM), filename)

        //Call the API.
        val (_, _, result) = Fuel.upload("${context.getString(R.string.basApi)}/staffenrolment", Method.POST , postBody)
            .add(FileDataPart(imageFile, name = "image", contentType = "image/jpeg"))
            .responseString()

        //If failed, then print exception. If successful, then print result.
        when (result) {

            is Result.Success -> {
                var response = result.get()


            }

            is Result.Failure -> {
                var error = result.getException()

                Log.e("user-log", "${error}")
            }

        }
    }


    fun syncErrolmentData(context: Context, staffData: StaffXX){

        val enrolBox =
            ObjectBox.store.boxFor(EnrolmentModel::class.java)

        if(staffData.staff_image !== null){
            DownloadImage().downloadFile(context, "${context.getString(R.string.mainLink)}/uploads/${staffData.staff_image}", staffData.staff_image)
        }


        var formData = with(staffData){
           EnrolmentModel(
               uuid = uuid,
               fullname = fullname,
               birthdate = ExtApi.StrToDateFormat(birthdate),
               fac_a_department = fac_a_department,
               work_position = work_position,
               staff_category = staff_category,
               gender = gender,
               unique_id_no = unique_id_no,
               username = username,
               password = "",
               email = email,
               mobile = mobile,
               left_fingerprint = left_fingerprint?.trim(),
               left_inpsize = 256,
               right_inpsize = 256,
               right_fingerprint = right_fingerprint?.trim(),
               nfc_card_code = nfc_card_code,
               enrolled_date = ExtApi.StrToDateFormat(enrolled_date),
               image = "${context.getExternalFilesDir(android.os.Environment.DIRECTORY_DCIM)!!}/${staff_image}",
               objectId = _id,
               work_hour = work_hour,
           )
       }
        Log.d("user-log", "${context.getString(R.string.mainLink)}/uploads/${staffData.staff_image}")
        enrolBox.put(formData)


    }

    fun getDeviceUUID(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }


    fun createUniqueMd4Value(userId: String): String {

        val date_time = DateTimeUtils.formatWithPattern(Date(), "yyyy-MM-dd")
        val dateString = date_time?.toString()
        val inputString = "$dateString$userId"

        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(inputString.toByteArray(charset("UTF-8")))
            val sb = StringBuffer()
            for (i in array.indices) {
                sb.append(Integer.toHexString(array[i] and 0xFF or 0x100).substring(1, 3))
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
        } catch (ex: UnsupportedEncodingException) {
        }
        return ""

    }


}