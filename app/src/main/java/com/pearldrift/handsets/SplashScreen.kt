package com.pearldrift.handsets

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fgtit.fpcore.StaffAuthClass
import com.lyft.kronos.KronosClock
import com.lyft.kronos.internal.ntp.SntpClient
import com.parse.ParseUser
import java.util.*
import kotlin.concurrent.schedule


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        val handler = Handler()
//        handler.postDelayed({
//            hidesplash()
//        }, 5000)
        Timer().schedule(5000){
            hidesplash()
        }



    }

    fun hidesplash (){


        val userid = StaffAuthClass().readFromSharedPreferences("userId", this)


        val intentMain =  Intent(this, MainActivity::class.java)
        val intentLogin =  Intent(this, LoginActivity::class.java)
        val bundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()

        if (userid != null) {
            startActivity(intentMain, bundle)
            finish()
        } else {
            startActivity(intentLogin, bundle)
            finish()
        }

    }

}