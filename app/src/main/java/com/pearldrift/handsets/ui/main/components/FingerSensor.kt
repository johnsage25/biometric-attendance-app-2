package com.pearldrift.handsets.ui.main.components

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.fgtit.device.Constants
import com.fgtit.device.FPModule

class FingerSensor() {

    var bmpdata = ByteArray(Constants.RESBMP_SIZE)
    var bmpsize = 0
    var refdata = ByteArray(Constants.TEMPLATESIZE * 2)
    var refsize = 0
    var matdata = ByteArray(Constants.TEMPLATESIZE * 2)
    var matsize = 0
    var worktype = 0

    private val fpm = FPModule()

    init {

        fpm.InitMatch()
        fpm.SetTimeOut(Constants.TIMEOUT_LONG)
        fpm.SetLastCheckLift(true)

    }


    fun openDevice(){
        fpm.OpenDevice()
    }

    fun closeDevice(){
        fpm.CloseDevice()
    }

    fun defineFpm(context: Context, mHandler: Handler?) {
        return fpm.SetContextHandler(context, mHandler)
    }

    fun GenerateTemplate(context: Context, count: Int){

        if (fpm.GenerateTemplate(count)) {

        } else {
            Toast.makeText(context, "Busy", Toast.LENGTH_SHORT).show()
        }

    }

    fun GetTemplateByGen(matdata: ByteArray): Int{
        return fpm.GetTemplateByGen(matdata)
    }

    fun MatchTemplate(refdata: ByteArray,refsize: Int,  matdata: ByteArray, matsize:Int): Boolean{
        return fpm.MatchTemplate(
            refdata,
            refsize,
            matdata,
            matsize,
            60
        )
    }

    private fun memcpy(
        dstbuf: ByteArray,
        dstoffset: Int,
        srcbuf: ByteArray,
        srcoffset: Int,
        size: Int
    ) {
        for (i in 0 until size) {
            dstbuf[dstoffset + i] = srcbuf[srcoffset + i]
        }
    }

    fun GetBmpImage(bmpdata: ByteArray): Int{
        return fpm.GetBmpImage(bmpdata)
    }

    fun getSize (lptemplate: ByteArray): Int{
        memcpy(lptemplate, 0, refdata, 0, refsize)
        return refsize
    }

    fun RestartTask(state: Boolean){
        fpm.RestartTask(state)
    }
}