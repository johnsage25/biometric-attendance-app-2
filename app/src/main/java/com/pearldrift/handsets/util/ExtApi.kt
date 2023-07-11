package com.pearldrift.handsets.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.nfc.NfcAdapter
import android.os.Environment
import android.text.format.Time
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.ByteArrayBuffer
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.URL
import java.net.URLConnection
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object ExtApi {
    val stringDate: String
        get() {
            val currentTime =
                Date(System.currentTimeMillis())
            val formatter =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return formatter.format(currentTime)
        }// 0-23

    //int second = t.second;
// or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO
    // ȡOOϵͳʱO䡣
    //Date dt = new Date(System.currentTimeMillis());
    //String id = String.format("%d%02d%02d%02d%02d",dt.getYear(),dt.getMonth(),dt.getDay(),dt.getHours(),dt.getMinutes());
    //return id;
    val dataTimeForID: String
        get() {
            //Date dt = new Date(System.currentTimeMillis());
            //String id = String.format("%d%02d%02d%02d%02d",dt.getYear(),dt.getMonth(),dt.getDay(),dt.getHours(),dt.getMinutes());
            //return id;
            val t = Time() // or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO
            t.setToNow() // ȡOOϵͳʱO䡣
            val year = t.year
            val month = t.month + 1
            val date = t.monthDay
            val hour = t.hour // 0-23
            val minute = t.minute
            //int second = t.second;
            return String.format("%d%02d%02d%02d%02d", year, month, date, hour, minute)
        }// 0-23  // or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO

    // ȡOOϵͳʱO䡣
    /*
      public static String getDataForID(){
          Time t=new Time(); // or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO
          t.setToNow(); // ȡOOϵͳʱO䡣
          int year = t.year;
          int month = t.month+1;
          int date = t.monthDay;
          return String.format("%d%02d%02d",year,month,date);
      }
      */
    val dataForID: String
        get() {
            val t = Time() // or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO
            t.setToNow() // ȡOOϵͳʱO䡣
            val year = t.year
            val month = t.month + 1
            val date = t.monthDay
            val hour = t.hour // 0-23
            val minute = t.minute
            return String.format("%02d%02d%02d%02d%02d", year - 2000, month, date, hour, minute)
        }// 0-23

    // or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO
    // ȡOOϵͳʱO䡣
    val dateTimeStr: String
        get() {
            val t = Time() // or Time t=new Time("GMT+8"); OOOOTime ZoneOOOϡO
            t.setToNow() // ȡOOϵͳʱO䡣
            val year = t.year
            val month = t.month + 1
            val date = t.monthDay
            val hour = t.hour // 0-23
            val minute = t.minute
            val second = t.second
            return String.format(
                "%d-%02d-%02d %02d:%02d:%02d",
                year,
                month,
                date,
                hour,
                minute,
                second
            )
        }

    fun IsFileExists(filename: String?): Boolean {
        val f = File(filename)
        return if (f.exists()) {
            true
        } else false
    }

    fun StrToDateFormat(str: String?): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = format.parse(str)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return date
    }


    fun DeleteFile(filename: String?) {
        val f = File(filename)
        if (f.exists()) {
            f.delete()
        }
    }

    fun LoadBitmap(res: Resources?, id: Int): Bitmap {
        //Resources res = getResources();
        return BitmapFactory.decodeResource(res, id)
    }

    fun Bytes2Bimap(b: ByteArray): Bitmap? {
        return if (b.size != 0) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else {
            null
        }
    }

    fun getImageDrawable(path: String?): BitmapDrawable? {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        val outStream = ByteArrayOutputStream()
        val bt = ByteArray(1024)
        val `in`: InputStream
        try {
            `in` = FileInputStream(file)
            var readLength = `in`.read(bt)
            while (readLength != -1) {
                outStream.write(bt, 0, readLength)
                readLength = `in`.read(bt)
            }
            val data = outStream.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size) // OOOOλͼ
            return BitmapDrawable(bitmap)
        } catch (e: Exception) {
        }
        return null
    }

    fun Base64ToBytes(txt: String?): ByteArray {
        return Base64.decode(txt, Base64.DEFAULT)
    }

    fun BytesToBase64(ba: ByteArray?, size: Int): String {
        return Base64.encodeToString(ba, Base64.DEFAULT)
    }

    fun saveImage(finalBitmap: Bitmap, filename: String?, context: Context) {
        val root = context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val myDir = File("$root")
        myDir.mkdirs()
        val fname = filename
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

//    fun saveImage(image: Bitmap, image_name: String, context: Context): String? {
//        var savedImagePath: String? = null
//        val imageFileName = image_name
//
//        val storageDir = context.getExternalFilesDir("/DCIM/");
//        var success = true
//        if (!storageDir!!.exists()) {
//            success = storageDir.mkdirs()
//        }
//        if (success) {
//            val imageFile = File(storageDir, imageFileName)
//            savedImagePath = imageFile.getAbsolutePath()
//            try {
//                val fOut: OutputStream = FileOutputStream(imageFile)
//                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
//
//                fOut.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            // Add the image to the system gallery
//
//            //Toast.makeText(this, "IMAGE SAVED", Toast.LENGTH_LONG).show() // to make this working, need to manage coroutine, as this execution is something off the main thread
//        }
//        return savedImagePath
//    }

    /**
     * OOOֽOOOO鱣OOΪһOOOļO
     * @EditTime 2007-8-13 OOOO11:45:56
     */
    fun SaveBytesToFile(b: ByteArray?, outputFile: String?): File? {
        var stream: BufferedOutputStream? = null
        var file: File? = null
        try {
            file = File(outputFile)
            val fstream = FileOutputStream(file)
            stream = BufferedOutputStream(fstream)
            stream.write(b)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
        return file
    }

    fun intToByte(number: Int): ByteArray {
        val abyte = ByteArray(4)
        // "&" O루ANDOOOOOOOOOOOOOͲOOOOOOжOӦλִOвOOOOOOOOOOOOOλOOΪ1ʱOOO1OOOOOO0OO
        abyte[0] = (0xff and number).toByte()
        // ">>"OOOOλOOOOΪOOOOOOOλOO0OOOOΪOOOOOOOλOO1
        abyte[1] = (0xff00 and number shr 8).toByte()
        abyte[2] = (0xff0000 and number shr 16).toByte()
        abyte[3] = (-0x1000000 and number shr 24).toByte()
        return abyte
    }

    /**
     * OOOOλOƵO byte[]תOOOOint
     * @param byte[] bytes
     * @return int  number
     */
//    fun bytesToInt(bytes: ByteArray): Int {
//        var number: Int = bytes[0] and 0xFF
//        // "|="OOλOOֵOO
//        number = number or (bytes[1] shl 8 and 0xFF00)
//        number = number or (bytes[2] shl 16 and 0xFF0000)
//        number = number or (bytes[3] shl 24 and -0x1000000)
//        return number
//    }


    /**
     * OOOOתOOOOJavaOַOOO
     * @param date
     * @return str
     */
    fun DateToStr(date: Date?): String {
        val format =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    val TABLE1021 = charArrayOf( /* CRC1021OOʽOO */
        0x0000.toChar(),
        0x1021.toChar(),
        0x2042.toChar(),
        0x3063.toChar(),
        0x4084.toChar(),
        0x50a5.toChar(),
        0x60c6.toChar(),
        0x70e7.toChar(),
        0x8108.toChar(),
        0x9129.toChar(),
        0xa14a.toChar(),
        0xb16b.toChar(),
        0xc18c.toChar(),
        0xd1ad.toChar(),
        0xe1ce.toChar(),
        0xf1ef.toChar(),
        0x1231.toChar(),
        0x0210.toChar(),
        0x3273.toChar(),
        0x2252.toChar(),
        0x52b5.toChar(),
        0x4294.toChar(),
        0x72f7.toChar(),
        0x62d6.toChar(),
        0x9339.toChar(),
        0x8318.toChar(),
        0xb37b.toChar(),
        0xa35a.toChar(),
        0xd3bd.toChar(),
        0xc39c.toChar(),
        0xf3ff.toChar(),
        0xe3de.toChar(),
        0x2462.toChar(),
        0x3443.toChar(),
        0x0420.toChar(),
        0x1401.toChar(),
        0x64e6.toChar(),
        0x74c7.toChar(),
        0x44a4.toChar(),
        0x5485.toChar(),
        0xa56a.toChar(),
        0xb54b.toChar(),
        0x8528.toChar(),
        0x9509.toChar(),
        0xe5ee.toChar(),
        0xf5cf.toChar(),
        0xc5ac.toChar(),
        0xd58d.toChar(),
        0x3653.toChar(),
        0x2672.toChar(),
        0x1611.toChar(),
        0x0630.toChar(),
        0x76d7.toChar(),
        0x66f6.toChar(),
        0x5695.toChar(),
        0x46b4.toChar(),
        0xb75b.toChar(),
        0xa77a.toChar(),
        0x9719.toChar(),
        0x8738.toChar(),
        0xf7df.toChar(),
        0xe7fe.toChar(),
        0xd79d.toChar(),
        0xc7bc.toChar(),
        0x48c4.toChar(),
        0x58e5.toChar(),
        0x6886.toChar(),
        0x78a7.toChar(),
        0x0840.toChar(),
        0x1861.toChar(),
        0x2802.toChar(),
        0x3823.toChar(),
        0xc9cc.toChar(),
        0xd9ed.toChar(),
        0xe98e.toChar(),
        0xf9af.toChar(),
        0x8948.toChar(),
        0x9969.toChar(),
        0xa90a.toChar(),
        0xb92b.toChar(),
        0x5af5.toChar(),
        0x4ad4.toChar(),
        0x7ab7.toChar(),
        0x6a96.toChar(),
        0x1a71.toChar(),
        0x0a50.toChar(),
        0x3a33.toChar(),
        0x2a12.toChar(),
        0xdbfd.toChar(),
        0xcbdc.toChar(),
        0xfbbf.toChar(),
        0xeb9e.toChar(),
        0x9b79.toChar(),
        0x8b58.toChar(),
        0xbb3b.toChar(),
        0xab1a.toChar(),
        0x6ca6.toChar(),
        0x7c87.toChar(),
        0x4ce4.toChar(),
        0x5cc5.toChar(),
        0x2c22.toChar(),
        0x3c03.toChar(),
        0x0c60.toChar(),
        0x1c41.toChar(),
        0xedae.toChar(),
        0xfd8f.toChar(),
        0xcdec.toChar(),
        0xddcd.toChar(),
        0xad2a.toChar(),
        0xbd0b.toChar(),
        0x8d68.toChar(),
        0x9d49.toChar(),
        0x7e97.toChar(),
        0x6eb6.toChar(),
        0x5ed5.toChar(),
        0x4ef4.toChar(),
        0x3e13.toChar(),
        0x2e32.toChar(),
        0x1e51.toChar(),
        0x0e70.toChar(),
        0xff9f.toChar(),
        0xefbe.toChar(),
        0xdfdd.toChar(),
        0xcffc.toChar(),
        0xbf1b.toChar(),
        0xaf3a.toChar(),
        0x9f59.toChar(),
        0x8f78.toChar(),
        0x9188.toChar(),
        0x81a9.toChar(),
        0xb1ca.toChar(),
        0xa1eb.toChar(),
        0xd10c.toChar(),
        0xc12d.toChar(),
        0xf14e.toChar(),
        0xe16f.toChar(),
        0x1080.toChar(),
        0x00a1.toChar(),
        0x30c2.toChar(),
        0x20e3.toChar(),
        0x5004.toChar(),
        0x4025.toChar(),
        0x7046.toChar(),
        0x6067.toChar(),
        0x83b9.toChar(),
        0x9398.toChar(),
        0xa3fb.toChar(),
        0xb3da.toChar(),
        0xc33d.toChar(),
        0xd31c.toChar(),
        0xe37f.toChar(),
        0xf35e.toChar(),
        0x02b1.toChar(),
        0x1290.toChar(),
        0x22f3.toChar(),
        0x32d2.toChar(),
        0x4235.toChar(),
        0x5214.toChar(),
        0x6277.toChar(),
        0x7256.toChar(),
        0xb5ea.toChar(),
        0xa5cb.toChar(),
        0x95a8.toChar(),
        0x8589.toChar(),
        0xf56e.toChar(),
        0xe54f.toChar(),
        0xd52c.toChar(),
        0xc50d.toChar(),
        0x34e2.toChar(),
        0x24c3.toChar(),
        0x14a0.toChar(),
        0x0481.toChar(),
        0x7466.toChar(),
        0x6447.toChar(),
        0x5424.toChar(),
        0x4405.toChar(),
        0xa7db.toChar(),
        0xb7fa.toChar(),
        0x8799.toChar(),
        0x97b8.toChar(),
        0xe75f.toChar(),
        0xf77e.toChar(),
        0xc71d.toChar(),
        0xd73c.toChar(),
        0x26d3.toChar(),
        0x36f2.toChar(),
        0x0691.toChar(),
        0x16b0.toChar(),
        0x6657.toChar(),
        0x7676.toChar(),
        0x4615.toChar(),
        0x5634.toChar(),
        0xd94c.toChar(),
        0xc96d.toChar(),
        0xf90e.toChar(),
        0xe92f.toChar(),
        0x99c8.toChar(),
        0x89e9.toChar(),
        0xb98a.toChar(),
        0xa9ab.toChar(),
        0x5844.toChar(),
        0x4865.toChar(),
        0x7806.toChar(),
        0x6827.toChar(),
        0x18c0.toChar(),
        0x08e1.toChar(),
        0x3882.toChar(),
        0x28a3.toChar(),
        0xcb7d.toChar(),
        0xdb5c.toChar(),
        0xeb3f.toChar(),
        0xfb1e.toChar(),
        0x8bf9.toChar(),
        0x9bd8.toChar(),
        0xabbb.toChar(),
        0xbb9a.toChar(),
        0x4a75.toChar(),
        0x5a54.toChar(),
        0x6a37.toChar(),
        0x7a16.toChar(),
        0x0af1.toChar(),
        0x1ad0.toChar(),
        0x2ab3.toChar(),
        0x3a92.toChar(),
        0xfd2e.toChar(),
        0xed0f.toChar(),
        0xdd6c.toChar(),
        0xcd4d.toChar(),
        0xbdaa.toChar(),
        0xad8b.toChar(),
        0x9de8.toChar(),
        0x8dc9.toChar(),
        0x7c26.toChar(),
        0x6c07.toChar(),
        0x5c64.toChar(),
        0x4c45.toChar(),
        0x3ca2.toChar(),
        0x2c83.toChar(),
        0x1ce0.toChar(),
        0x0cc1.toChar(),
        0xef1f.toChar(),
        0xff3e.toChar(),
        0xcf5d.toChar(),
        0xdf7c.toChar(),
        0xaf9b.toChar(),
        0xbfba.toChar(),
        0x8fd9.toChar(),
        0x9ff8.toChar(),
        0x6e17.toChar(),
        0x7e36.toChar(),
        0x4e55.toChar(),
        0x5e74.toChar(),
        0x2e93.toChar(),
        0x3eb2.toChar(),
        0x0ed1.toChar(),
        0x1ef0.toChar()
    )




    /**
     * OַOOOתOOOOOOOO
     * @param str
     * @return date
     */
    fun StrToDate(str: String?): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date? = null
        try {
            date = format.parse(str)
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return date
    }

    // OOťOOOOOO
    private val BUTTON_PRESSED = floatArrayOf(
        1.1f, 0f, 0f, 0f, -50f, 0f, 1.1f, 0f, 0f, -50f, 0f, 0f, 1.1f, 0f, -50f, 0f, 0f, 0f, 1.5f, 0f
    )

    // OOťOָOԭ״
    private val BUTTON_RELEASED =
        floatArrayOf(1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f)
    private val touchListener = OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            v.background.colorFilter = ColorMatrixColorFilter(BUTTON_PRESSED)
            v.setBackgroundDrawable(v.background)
        } else if (event.action == MotionEvent.ACTION_UP) {
            v.background.colorFilter = ColorMatrixColorFilter(BUTTON_RELEASED)
            v.setBackgroundDrawable(v.background)
        }
        false
    }

    fun setButtonStateChangeListener(v: View) {
        v.setOnTouchListener(touchListener)
    }

    fun IsHaveSdCard(): Boolean {
        val status = Environment.getExternalStorageState()
        return if (status == Environment.MEDIA_MOUNTED) {
            true
        } else {
            false
        }
    }

    fun rememberState (state:Int, context: Activity): Boolean{
        val sharedPref =  context.getPreferences(Context.MODE_PRIVATE)
        var sta = sharedPref.getInt("counter", 0)
        return sta > 0
    }

    fun setState(state: Int, context: Activity){
        val sharedPref =  context.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt("counter", state)
            commit()
        }
    }

    fun CreateDir(dirpath: String?): Boolean {
        return if (IsHaveSdCard()) {
            val destDir = File(dirpath)
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            true
        } else {
            false
        }
    }

    fun getFileName(apath: String): String? {
        /*
        int start=apath.lastIndexOf("/");
        int end=apath.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return apath.substring(start+1,end);
        }else{
            return null;
        }
        */
        val start = apath.lastIndexOf("/")
        return if (start != -1) {
            apath.substring(start + 1)
        } else {
            null
        }
    }

    /**
     * make true current connect service is wifi
     * @param mContext
     * @return
     */
    fun IsWifi(mContext: Context): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return if (activeNetInfo != null
            && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
        ) {
            true
        } else false
    }

    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    /**
     * OǷOOO wifi trueOOOOOO falseOOOرO
     *
     * һOOҪOOOOȨOޣO <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
     *
     *
     * @param isEnable
     */
    fun setWifi(context: Context, isEnable: Boolean) {
        val mWm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (isEnable) {
            if (!mWm.isWifiEnabled) {
                mWm.isWifiEnabled = true
            }
        } else {
            if (mWm.isWifiEnabled) {
                mWm.isWifiEnabled = false
            }
        }
    }

    fun getWifi(context: Context): Boolean {
        val mWm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return mWm.isWifiEnabled
    }


    fun getMobileDataStatus(context: Context): Boolean {
        val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var conMgrClass: Class<*>? = null
        var iConMgrField: Field? = null
        var iConMgr: Any? = null
        var iConMgrClass: Class<*>? = null
        var getMobileDataEnabledMethod: Method? = null
        try {
            conMgrClass = Class.forName(conMgr.javaClass.name)
            iConMgrField = conMgrClass.getDeclaredField("mService")
            iConMgrField.isAccessible = true
            iConMgr = iConMgrField[conMgr]
            iConMgrClass = Class.forName(iConMgr.javaClass.name)
            getMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("getMobileDataEnabled")
            getMobileDataEnabledMethod.isAccessible = true
            return getMobileDataEnabledMethod.invoke(iConMgr) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }



    fun IsSupportNFC(context: Context?): Boolean {
        return if (NfcAdapter.getDefaultAdapter(context) == null) {
            false
        } else true
    }

    fun SaveDataToFile(filename: String?, data: ByteArray?) {
        val f = File(filename)
        if (f.exists()) {
            f.delete()
        }
        File(filename)
        try {
            val randomFile = RandomAccessFile(filename, "rw")
            val fileLength = randomFile.length()
            randomFile.seek(fileLength)
            randomFile.write(data)
            randomFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun uriToImage(url:String): ByteArray? {

        try {
            val imageUrl = URL(url)
            val ucon: URLConnection = imageUrl.openConnection()
            val `is`: InputStream = ucon.getInputStream()
            val bis = BufferedInputStream(`is`)
            val baf = ByteArrayBuffer(500)
            var current = 0
            while (bis.read().also { current = it } != -1) {
                baf.append(current!!.toByte().toInt())
            }
            return baf.toByteArray()
        } catch (e: java.lang.Exception) {
            Log.d("ImageManager", "Error: $e")
        }
        return null

    }

    fun randomID(): String = List(16) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

    fun LoadDataFromFile(filename: String?): ByteArray? {
        val f = File(filename)
        if (!f.exists()) {
            return null
        }
        try {
            val randomFile = RandomAccessFile(filename, "rw")
            val fileLength = randomFile.length().toInt()
            val content = ByteArray(fileLength)
            randomFile.read(content)
            randomFile.close()
            return content
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    val stringDate1: String
        get() {
            val date = Date()
            val sdformat =
                SimpleDateFormat("HH:mma yyyy/MM/dd", Locale.ENGLISH)
            return sdformat.format(date)
        }
}