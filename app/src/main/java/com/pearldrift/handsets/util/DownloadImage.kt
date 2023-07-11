package com.pearldrift.handsets.util
import android.content.Context
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.BlobDataPart
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.httpUpload
import com.github.kittinunf.result.Result
import java.io.File
import java.util.concurrent.TimeUnit

class DownloadImage {



    fun downloadFile(context: Context,link:String, filename: String) {


       try {
           var imageData = ExtApi.uriToImage(link)
           var bitmap = ExtApi.saveImage(Glide.with(context)
               .asBitmap()
               .load("${link}") // sample image
               .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
               .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
               .submit()
               .get(),
               filename,
               context
           )

       } catch (e: Exception){
           Log.e("user-log", "${e}");
       }
    }


}