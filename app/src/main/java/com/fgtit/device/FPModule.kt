package com.fgtit.device

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.fpi.GpioControl
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.os.SystemClock
import android.util.Log
import com.fgtit.fpcore.FPMatch
import java.util.*
import kotlin.concurrent.schedule

class FPModule {
    var bmpdata = ByteArray(Constants.RESBMP_SIZE) //73728+1078=74806
    var bmpsize = IntArray(1)
    var rawdata = ByteArray(Constants.RESRAW_SIZE) //73728
    var rawsize = IntArray(1)
    var refdata = ByteArray(2048)
    var refsize = 0
    var tmpdata = ByteArray(512)
    var tmpsize = IntArray(1)
    private var mDeviceType = Constants.DEV_UNKNOW
    private var mDeviceIO = Constants.DEV_IO_UNKNOW
    private var mBaudRate = 0
    private var intentAction = ""
    private var mUartName = ""
    private var pContext: Context? = null
    private var mHandler: Handler? = null
    private var handler: Handler?=null
    private val m_ImageType = 0
    var killrunner = false
    var workThread : Thread? = null
    @Volatile
    private var bCancel = false

    @Volatile
    private var isopening = false

    @Volatile
    private var isworking = false

    @Volatile
    private var isCheckLift = false

    @Volatile
    private var lastCheckLift = false
    private var captureCount = 0
    private var captureIndex = 0
    private var captureTimeout = 0
    private var timeoutCount = 0
    private var mPermissionIntent: PendingIntent? = null
    private var filter: IntentFilter? = null
    private val serialModule: SerialModule
    private lateinit var usbModule: UsbModule
    private val highModule: HighModule
    //8'
    private var onGetImageListener: SerialModule.OnGetImageListener? =
        null

    private var onUpImageListener: SerialModule.OnUpImageListener? = null

    private var onGenCharListener: SerialModule.OnGenCharListener? = null

    private var onRegModelListener: SerialModule.OnRegModelListener? =
        null

    private var onUpCharListener: SerialModule.OnUpCharListener? = null

    private var onDownCharListener: SerialModule.OnDownCharListener? =
        null

    private var onMatchListener: SerialModule.OnMatchListener? = null

    private var onStoreCharListener: SerialModule.OnStoreCharListener? =
        null

    private var onLoadCharListener: SerialModule.OnLoadCharListener? =
        null

    private var onSearchListener: SerialModule.OnSearchListener? = null

    private var onDeleteCharListener:SerialModule.OnDeleteCharListener? =
        null

    private var onEmptyListener: SerialModule.OnEmptyListener? = null

    private var onEnrollListener: SerialModule.OnEnrollListener? = null

    private var onIdentifyListener: SerialModule.OnIdentifyListener? =
        null

    private var onGetImageExListener: SerialModule.OnGetImageExListener? =
        null
    private var onUpImageExListener: SerialModule.OnUpImageExListener? =
        null
    private var onGenCharExListener: SerialModule.OnGenCharExListener? =
        null
    //7'

    //6'

    //Access

    //7.0
    val deviceType: Int
        get() {
            Log.d("FPModule", "come in ")
            val devname = Build.MODEL
            val devid = Build.DEVICE
            val devmodel = Build.DISPLAY

            //8'
            if (devname == "FP08" || devname == "FP-08" || devname == "FP-08T" || devname == "TIQ-805Q") {
                if (devname == "FP-08T") {
                    return Constants.DEV_8_4G_UART
                } else if (devmodel.indexOf("35SM") >= 0) {
                    return Constants.DEV_8_4G_USB
                }
                return Constants.DEV_8_WIFI_USB
            }

            //7'
            if (devname == "b82" || devname == "FP07" || devname == "FP-07") {
                return if (devname == "b82") {
                    Constants.DEV_7_3G_SPI
                } else {
                    if (devid == "b906") return Constants.DEV_7_3G_SPI else if (devmodel.indexOf("35SM") >= 0) return Constants.DEV_7_4G_USB else if (devmodel.indexOf(
                            "80M"
                        ) >= 0
                    ) return Constants.DEV_7_3G_USB
                    Constants.DEV_7_3G_SPI
                }
            }

            //6'
            if (devname == "FP06" || devname == "FP-06") {
                return Constants.DEV_6_4G_UART
            }

            //Access
            if (devname == "FT06" || devname == "FT-06") {
                return Constants.DEV_5_3G_UART_AC
            }
            if (devname == "HF-A5") {
                return Constants.DEV_5_3G_UART_A5
            }

            //7.0
            if (devname == "FP--05") {
                return Constants.DEV_5_4G_UART_AC
            }
            if (devname == "mbk82_tb_kk" || devname == "iMA122" || devname == "iMA321" || devname == "iMA322" || devname == "BioMatch FM-01" || devname == "FP05" || devname == "FP-05" || devname == "KT-7500") {
                return if (devmodel.indexOf("35SM") >= 0) {
                    Constants.DEV_5_4G_UART_A6
                } else if (devmodel.indexOf("80M") >= 0) {
                    Constants.DEV_5_3G_UART_A6
                } else if (devmodel.indexOf("37SM") >= 0) {
                    Constants.DEV_5_4G_UART_A6
                } else {
                    if (devmodel.substring(0, 15) == "FGT_FP05_v1.1.2") {
                        Constants.DEV_5_3G_UART_340M
                    } else Constants.DEV_5_3G_UART_A4
                }
            }
            return if (devmodel.indexOf("37SM") >= 0) {
                Constants.DEV_5_4G_UART_A6
            } else Constants.DEV_UNKNOW
        }

    fun getDeviceIO(devType: Int): Int {
        when (devType) {
            Constants.DEV_5_3G_UART_A4, Constants.DEV_5_3G_UART_340M, Constants.DEV_5_4G_UART_A6, Constants.DEV_5_3G_UART_A6, Constants.DEV_5_3G_UART_AC, Constants.DEV_5_3G_UART_A5, Constants.DEV_5_4G_UART_AC, Constants.DEV_6_4G_UART, Constants.DEV_8_4G_UART -> return Constants.DEV_IO_UART
            Constants.DEV_7_3G_SPI -> return Constants.DEV_IO_SPI
            Constants.DEV_8_WIFI_USB, Constants.DEV_8_4G_USB, Constants.DEV_7_3G_USB, Constants.DEV_7_4G_USB -> return Constants.DEV_IO_USB
        }
        return Constants.DEV_IO_UNKNOW
    }

    fun getUartName(devType: Int): String {
        when (devType) {
            Constants.DEV_5_3G_UART_A4 -> return "/dev/ttyMT3"
            Constants.DEV_5_3G_UART_340M -> return "/dev/ttyMT1"
            Constants.DEV_7_3G_SPI -> return "/dev/spidev0.0"
            Constants.DEV_5_4G_UART_A6 -> return "/dev/ttyMT1"
            Constants.DEV_5_3G_UART_A6 -> return "/dev/ttyMT1"
            Constants.DEV_5_3G_UART_AC -> return "/dev/ttyMT1"
            Constants.DEV_5_3G_UART_A5 -> return "/dev/ttyMT1"
            Constants.DEV_5_4G_UART_AC -> return "/dev/ttyMT1"
            Constants.DEV_6_4G_UART, Constants.DEV_8_4G_UART -> return "/dev/ttyMT2"
        }
        return ""
    }

    fun getUartBaudRate(devType: Int): Int {
        if (Constants.DEV_8_4G_UART == devType) {
            return 921600
        }
        return if (Constants.DEV_7_3G_SPI == devType) {
            2000 * 1000
        } else {
            460800
        }
    }

    fun PowerControl(bOn: Boolean): Boolean {
        when (mDeviceType) {
            Constants.DEV_5_3G_UART_A4 -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(119, 0)
                        ca.setGpioDir(119, 1)
                        ca.setGpioOut(119, 1)
                    } else {
                        ca.setGpioMode(119, 0)
                        ca.setGpioDir(119, 1)
                        ca.setGpioOut(119, 0)
                    }
                }
                return true
            }
            Constants.DEV_5_3G_UART_340M -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(65, 0)
                        ca.setGpioDir(65, 1)
                        ca.setGpioOut(65, 1)
                    } else {
                        ca.setGpioMode(65, 0)
                        ca.setGpioDir(65, 1)
                        ca.setGpioOut(65, 0)
                    }
                }
                return true
            }
            Constants.DEV_5_4G_UART_A6 -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 1)
                    } else {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 0)
                    }
                }
                return true
            }
            Constants.DEV_5_3G_UART_A6 -> {
                run {
                    Log.d("FPModule", "??????")
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(65, 0)
                        ca.setGpioDir(65, 1)
                        ca.setGpioOut(65, 1)
                    } else {
                        ca.setGpioMode(65, 0)
                        ca.setGpioDir(65, 1)
                        ca.setGpioOut(65, 0)
                    }
                }
                return true
            }
            Constants.DEV_5_3G_UART_AC -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(14, 0)
                        ca.setGpioDir(14, 1)
                        ca.setGpioOut(14, 1)
                    } else {
                        ca.setGpioMode(14, 0)
                        ca.setGpioDir(14, 1)
                        ca.setGpioOut(14, 0)
                    }
                }
                return true
            }
            Constants.DEV_5_3G_UART_A5 -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(15, 0)
                        ca.setGpioDir(15, 1)
                        ca.setGpioOut(15, 1)
                    } else {
                        ca.setGpioMode(15, 0)
                        ca.setGpioDir(15, 1)
                        ca.setGpioOut(15, 0)
                    }
                }
                return true
            }
            Constants.DEV_5_4G_UART_AC -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(59, 0)
                        ca.setGpioDir(59, 1)
                        ca.setGpioOut(59, 1)
                    } else {
                        ca.setGpioMode(59, 0)
                        ca.setGpioDir(59, 1)
                        ca.setGpioOut(59, 0)
                    }
                }
                return true
            }
            Constants.DEV_6_4G_UART -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 1)
                    } else {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 0)
                    }
                }
                return true
            }
            Constants.DEV_7_3G_SPI -> {
                run {}
                return true
            }
            Constants.DEV_7_3G_USB -> {
                run {}
                return true
            }
            Constants.DEV_7_4G_USB -> {
                run {}
                return true
            }
            Constants.DEV_8_WIFI_USB -> {
                run {}
                return true
            }
            Constants.DEV_8_4G_USB -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 1)
                    } else {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 0)
                    }
                }
                return true
            }
            Constants.DEV_8_4G_UART -> {
                run {
                    val ca = GpioControl()
                    if (bOn) {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 1)
                    } else {
                        ca.setGpioMode(54, 0)
                        ca.setGpioDir(54, 1)
                        ca.setGpioOut(54, 0)
                    }
                }
                return true
            }
        }
        return false
    }

    fun OpenDevice() {
        bCancel = false
        isopening = false
        isworking = false
        if (mDeviceIO == Constants.DEV_IO_USB) {
            if (mDeviceType == Constants.DEV_8_WIFI_USB) {
                requestPermission()
            } else {
                PowerControl(true)
            }
        } else if (mDeviceIO == Constants.DEV_IO_SPI) {
            PowerControl(true)
            isopening = if (serialModule.OpenDevice(mUartName, mBaudRate, true)) {
                serialModule.Cancel(false)
                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_OK)
                true
            } else {
                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                false
            }
        } else if (mDeviceIO == Constants.DEV_IO_UART) {
            PowerControl(true)
            isopening = if (mDeviceType == Constants.DEV_8_4G_UART) {
                if (highModule.OpenDevice(mUartName, mBaudRate)) {
                    highModule.Cancel(false)
                    PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_OK)
                    true
                } else {
                    PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                    false
                }
            } else {
                if (serialModule.OpenDevice(mUartName, mBaudRate, false)) {
                    serialModule.Cancel(false)
                    PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_OK)
                    true
                } else {
                    PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                    false
                }
            }
        }
    }

    fun closeDeviceFast(){
        usbModule.CloseDevice()
    }
    fun CloseDevice() {
        bCancel = true
        isopening = false
        isworking = false
        if (mDeviceIO == Constants.DEV_IO_USB) {
            if (mDeviceType == Constants.DEV_8_WIFI_USB) {
                usbModule.CloseDevice()
            } else {
                PowerControl(false)
            }
        } else if (mDeviceIO == Constants.DEV_IO_SPI) {
            serialModule.Cancel(true)
            serialModule.CloseDevice()
            PowerControl(false)
        } else if (mDeviceIO == Constants.DEV_IO_UART) {
            if (mDeviceType == Constants.DEV_8_4G_UART) {
                highModule.Cancel(true)
                highModule.CloseDevice()
            } else {
                serialModule.Cancel(true)
                serialModule.CloseDevice()
            }
            PowerControl(false)
        }
        PostNclMsg(Constants.FPM_LIFT, Constants.DEV_CLOSE)
    }

    fun SetContextHandler(parentContext: Context?, handler: Handler?) {

        pContext = parentContext
        mHandler = handler
        usbModule.SetInstance(pContext)

    }

    fun stopHandler(){
        workThread?.let { mHandler?.removeCallbacks(it) };
    }

    private fun PostNclMsg(sdkmsg: Int, psdkresult: Int) {
        mHandler!!.obtainMessage(sdkmsg, psdkresult, -1).sendToTarget()
    }

    fun ResumeRegister() {
        mPermissionIntent = PendingIntent.getBroadcast(
            pContext, 0,
            Intent(ACTION_USB_PERMISSION), 0
        )
        filter = IntentFilter(ACTION_USB_PERMISSION)
        filter!!.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter!!.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        pContext!!.registerReceiver(mUsbReceiver, filter)
    }

    fun PauseUnRegister() {
        if (mUsbReceiver != null) {
            pContext!!.unregisterReceiver(mUsbReceiver)
        }
    }

    fun requestPermission() {
        val pmusbManager =
            (pContext)!!.getSystemService(Context.USB_SERVICE) as UsbManager
        if (pmusbManager == null) {
            PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
            return
        }
        var pmusbDevice: UsbDevice? = null
        val devlist = pmusbManager.deviceList
        val deviter: Iterator<UsbDevice> = devlist.values.iterator()
        while (deviter.hasNext()) {
            val tmpusbdev = deviter.next()
            Log.i("xpb", "find=" + tmpusbdev.vendorId.toString())
            if (tmpusbdev.vendorId == 0x0453 && tmpusbdev.productId == 0x9005) {
                Log.i("xpb", "usb=0x0453,0x9005")
                pmusbDevice = tmpusbdev
                break
            } else if (tmpusbdev.vendorId == 0x2009 && tmpusbdev.productId == 0x7638) {
                Log.i("xpb", "usb=0x2009,0x7638")
                pmusbDevice = tmpusbdev
                break
            } else if (tmpusbdev.vendorId == 0x2109 && tmpusbdev.productId == 0x7638) {
                Log.i("xpb", "usb=0x2109,0x7638")
                pmusbDevice = tmpusbdev
                break
            } else if (tmpusbdev.vendorId == 0x0483 && tmpusbdev.productId == 0x5720) {
                Log.i("xpb", "usb=0x0483,0x5720")
                pmusbDevice = tmpusbdev
                break
            }
        }
        if (pmusbDevice != null) {
            if (!pmusbManager.hasPermission(pmusbDevice)) {
                synchronized(mUsbReceiver!!) {
                    pmusbManager.requestPermission(
                        pmusbDevice,
                        mPermissionIntent
                    )
                }
                //Toast.makeText(pContext, "����Ȩ��", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(pContext, "����Ȩ��", Toast.LENGTH_LONG).show();
                usbModule.CloseDevice()
                isopening = if (usbModule.OpenDevice() == 0) {
                    PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_OK)
                    true
                } else {
                    PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                    false
                }
            }
        }
    }

    private val mUsbReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d("user", "ac ${action}")
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //Toast.makeText(pContext, "Ȩ���Ѿ�����", Toast.LENGTH_LONG).show();
                            usbModule.CloseDevice()
                            isopening = if (usbModule.OpenDevice() == 0) {
                                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_OK)
                                true
                            } else {
                                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                                false
                            }
                        } else {
                            PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_NOFOUND)
                        }
                    } else {
                        PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_ATTACHED)
                requestPermission()
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_DETACHED)
                CloseDevice()
            }
        }
    }

    fun InitMatch(): Int {
        return FPMatch.getInstance().InitMatch()
    }



    fun MatchTemplate(refdat: ByteArray?, refsize: Int, matdat: ByteArray?, matsize: Int,
        score: Int
    ): Boolean {
        val rc = refsize / 256
        val mc = matsize / 256
        val ref = ByteArray(256)
        val mat = ByteArray(256)
        for (i in 0 until mc) {

            System.arraycopy(matdat, i * 256, mat, 0, 256)
            for (j in 0 until rc) {
                System.arraycopy(refdat, j * 256, ref, 0, 256)
                if (FPMatch.getInstance().MatchTemplate(ref, mat) >= score) {
                    return true
                }
            }

        }
        return false
    }


    protected fun SerialModuleInit() {
        serialModule.setOnGetImageListener(object : SerialModule.OnGetImageListener {
            override fun onGetImageSuccess() {
                Log.d("user", "module lift")
                if (isCheckLift) {
                    serialModule.FP_GetImage()
                } else {
                    PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                    serialModule.FP_UpImage()
                }
            }

            override fun onGetImageFail() {
                captureTimeout++
                if (captureTimeout > timeoutCount) {
                    PostNclMsg(Constants.FPM_TIMEOUT, Constants.RET_FAIL)
                    isworking = false
                    serialModule.Cancel(true)
                    return
                }
                if (bCancel) {
                    bCancel = false
                    isworking = false
                    serialModule.Cancel(true)
                    Log.i("xpb", "bCancel Get Image")
                } else {
                    if (isCheckLift) {
                        isCheckLift = false
                        if (lastCheckLift) {
                            if (captureIndex >= captureCount) {
                                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                                isworking = true
                                return
                            }
                        }
                        PostNclMsg(Constants.FPM_PLACE, Constants.RET_OK)
                    }
                    serialModule.FP_GetImage()
                }
            }
        })
        serialModule.setOnUpImageListener(object : SerialModule.OnUpImageListener {
            override fun onUpImageSuccess(data: ByteArray) {
                Log.i("whw", "up image data.length=" + data.size)
                bmpsize[0] = data.size
                System.arraycopy(data, 0, bmpdata, 0, data.size)
                PostNclMsg(Constants.FPM_NEWIMAGE, 0)
                serialModule.FP_GenChar(1)
            }

            override fun onUpImageFail() {
                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                isworking = false
            }
        })
        serialModule.setOnGenCharListener(object : SerialModule.OnGenCharListener {
            override fun onGenCharSuccess(bufferId: Int) {
                serialModule.FP_UpChar()
            }

            override fun onGenCharFail() {
                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                isworking = false
            }
        })
        serialModule.setOnUpCharListener(object : SerialModule.OnUpCharListener {
            override fun onUpCharSuccess(model: ByteArray) {

                if (captureCount == 1) {
                    refsize = 256
                    System.arraycopy(model, 0, refdata, 0, model.size)
                    captureIndex++
                    if (lastCheckLift) {
                        isCheckLift = true
                        PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                        serialModule.FP_GetImage()
                        captureTimeout = 0
                    } else {
                        PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                        isworking = false
                    }
                } else {
                    refsize = (captureIndex + 1) * 256
                    System.arraycopy(model, 0, refdata, captureIndex * 256, 256)
                    captureIndex++
                    if (captureIndex >= captureCount) {
                        if (lastCheckLift) {
                            isCheckLift = true
                            PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                            serialModule.FP_GetImage()
                            captureTimeout = 0
                        } else {
                            PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                            isworking = false
                        }
                    } else {
                        isCheckLift = true
                        PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                        serialModule.FP_GetImage()
                        captureTimeout = 0
                    }
                }
            }

            override fun onUpCharFail() {
                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                isworking = false
            }
        })
    }

    protected fun HighModuleInit() {
        highModule.setOnGetImageListener(object : HighModule.OnGetImageListener {
            override fun onGetImageSuccess() {

                if (isCheckLift) {
                    highModule.FP_GetImage()
                } else {
                    PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                    highModule.FP_UpImage()
                }
            }

            override fun onGetImageFail() {
                captureTimeout++
                if (captureTimeout > timeoutCount) {
                    PostNclMsg(Constants.FPM_TIMEOUT, Constants.RET_FAIL)
                    isworking = false
                    highModule.Cancel(true)
                    return
                }
                if (bCancel) {
                    isworking = false
                    bCancel = false
                    highModule.Cancel(true)
                    Log.i("xpb", "bCancel Get Image")
                } else {
                    if (isCheckLift) {
                        isCheckLift = false
                        if (lastCheckLift) {
                            if (captureIndex >= captureCount) {
                                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                                isworking = false
                                return
                            }
                        }
                        PostNclMsg(Constants.FPM_PLACE, Constants.RET_OK)
                    }
                    highModule.FP_GetImage()
                }
            }
        })
        highModule.setOnUpImageListener(object : HighModule.OnUpImageListener {
            override fun onUpImageSuccess(data: ByteArray) {
                Log.i("whw", "up image data.length=" + data.size)
                bmpsize[0] = data.size
                System.arraycopy(data, 0, bmpdata, 0, data.size)
                PostNclMsg(Constants.FPM_NEWIMAGE, 0)
                highModule.FP_GenChar(1)
            }

            override fun onUpImageFail() {
                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                isworking = false
            }
        })
        highModule.setOnGenCharListener(object : HighModule.OnGenCharListener {

            override fun onGenCharSuccess(bufferId: Int) {
                highModule.FP_UpChar()
            }

            override fun onGenCharFail() {
                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)

                isworking = false
            }
        })
        highModule.setOnUpCharListener(object : HighModule.OnUpCharListener {
            override fun onUpCharSuccess(model: ByteArray) {

                Log.d("user", "FPM_LIFT")
                if (captureCount == 1) {
                    refsize = 256
                    System.arraycopy(model, 0, refdata, 0, model.size)
                    captureIndex++
                    if (lastCheckLift) {
                        isCheckLift = true
                        PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                        highModule.FP_GetImage()
                        captureTimeout = 0
                    } else {
                        PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                        isworking = false
                    }
                } else {
                    refsize = (captureIndex + 1) * 256
                    System.arraycopy(model, 0, refdata, captureIndex * 256, 256)
                    captureIndex++
                    if (captureIndex >= captureCount) {
                        if (lastCheckLift) {
                            isCheckLift = true
                            PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                            highModule.FP_GetImage()
                            captureTimeout = 0
                        } else {
                            PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                            isworking = false
                        }
                    } else {
                        isCheckLift = true
                        PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                        highModule.FP_GetImage()
                        captureTimeout = 0
                    }
                }
                isworking = false
            }

            override fun onUpCharFail() {
                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                isworking = false
            }
        })
    }


    inner class WorkThread : Runnable {
        fun CheckFingerLift(): Boolean {
            var ret = 1
            var timeout = 0
            while (true) {
                ret = usbModule.FxGetImage(m_ImageType, -0x1)
                if (ret != 0) return true
                SystemClock.sleep(100)
                timeout++
                if (timeout > timeoutCount) {
                    PostNclMsg(Constants.FPM_TIMEOUT, Constants.RET_FAIL)
                    isworking = false
                    return false
                }
                if (bCancel) {
                    bCancel = false
                    isworking = false
                    return false
                }
            }
        }

        override fun run() {
            if (killrunner) {

                return
            }
            else

            Log.d("user", "working")
            var timeout = 0
            if (!isopening) {
                PostNclMsg(Constants.FPM_DEVICE, Constants.DEV_FAIL)
                isworking = false
                return
            }
            captureIndex = 0
            while (captureIndex < captureCount) {
                PostNclMsg(Constants.FPM_PLACE, Constants.RET_OK)
                var ret = 1
                while (true) {
                    ret = usbModule.FxGetImage(m_ImageType, -0x1)
                    if (ret == 0) break
                    SystemClock.sleep(100)
                    timeout++
                    if (timeout > timeoutCount) {
                        PostNclMsg(Constants.FPM_TIMEOUT, Constants.RET_FAIL)
                        isworking = false
                        return
                    }
                    if (bCancel) {
                        bCancel = false
                        isworking = false
                        return
                    }
                }
                PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                if (usbModule.FxUpImage(m_ImageType, -0x1, rawdata, rawsize) == 0) {
                    usbModule.FPImageToBitmap(m_ImageType, rawdata, bmpdata)
                    PostNclMsg(Constants.FPM_NEWIMAGE, 0)
                } else {
                    PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                    isworking = false
                    return
                }

                if (usbModule.FxGenChar(m_ImageType, -0x1, 0x01) != 0) {
                    tmpsize[0] = 0
                    PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                    isworking = false
                    return
                }

                if (usbModule.FPUpChar(-0x1, 0x01, tmpdata, tmpsize) == 0) {

                    if (captureCount == 1) {
                        if (lastCheckLift) {
                            if (!CheckFingerLift()) {
                                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                            }
                        }
                        refsize = 256
                        System.arraycopy(tmpdata, 0, refdata, 0, 256)
                        PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                        isworking = false
                        return
                    } else {

                        refsize = (captureIndex + 1) * 256
                        System.arraycopy(tmpdata, 0, refdata, captureIndex * 256, 256)
                        if (captureIndex + 1 < captureCount) {
                            PostNclMsg(Constants.FPM_LIFT, Constants.RET_OK)
                            if (!CheckFingerLift()) {
                                PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                            }
                        } else {
                            if (lastCheckLift) {
                                if (!CheckFingerLift()) {
                                    PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                                }
                            }
                            PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_OK)
                        }

                    }
                } else {
                    PostNclMsg(Constants.FPM_GENCHAR, Constants.RET_FAIL)
                }
                captureIndex++
            }
            isworking = false
            return
        }
    }

    fun killRunder (starter: Boolean) {
        killrunner = starter;
    }

    fun setOnGetImageListener(onGetImageListener: SerialModule.OnGetImageListener?) {
        this.onGetImageListener = onGetImageListener
    }

    fun setOnUpImageListener(onUpImageListener: SerialModule.OnUpImageListener?) {
        this.onUpImageListener = onUpImageListener
    }

    fun setOnGenCharListener(onGenCharListener: SerialModule.OnGenCharListener?) {
        this.onGenCharListener = onGenCharListener
    }

    fun setOnRegModelListener(onRegModelListener: SerialModule.OnRegModelListener?) {
        this.onRegModelListener = onRegModelListener
    }

    fun setOnUpCharListener(onUpCharListener: SerialModule.OnUpCharListener?) {
        this.onUpCharListener = onUpCharListener
    }

    fun setOnDownCharListener(onDownCharListener: SerialModule.OnDownCharListener?) {
        this.onDownCharListener = onDownCharListener
    }

    fun setOnMatchListener(onMatchListener: SerialModule.OnMatchListener?) {
        this.onMatchListener = onMatchListener
    }

    fun setOnStoreCharListener(onStoreCharListener: SerialModule.OnStoreCharListener?) {
        this.onStoreCharListener = onStoreCharListener
    }

    fun setOnLoadCharListener(onLoadCharListener: SerialModule.OnLoadCharListener?) {
        this.onLoadCharListener = onLoadCharListener
    }

    fun setOnSearchListener(onSearchListener: SerialModule.OnSearchListener?) {
        this.onSearchListener = onSearchListener
    }

    fun setOnDeleteCharListener(
        onDeleteCharListener: SerialModule.OnDeleteCharListener?
    ) {
        this.onDeleteCharListener = onDeleteCharListener
    }

    fun setOnEmptyListener(onEmptyListener: SerialModule.OnEmptyListener?) {
        this.onEmptyListener = onEmptyListener
    }

    fun setOnEnrollListener(onEnrollListener: SerialModule.OnEnrollListener?) {
        this.onEnrollListener = onEnrollListener
    }

    fun setOnIdentifyListener(onIdentifyListener: SerialModule.OnIdentifyListener?) {
        this.onIdentifyListener = onIdentifyListener
    }

    fun setOnGetImageExListener(onGetImageExListener: SerialModule.OnGetImageExListener?) {
        this.onGetImageExListener = onGetImageExListener
    }

    fun setOnUpImageExListener(onUpImageExListener: SerialModule.OnUpImageExListener?) {
        this.onUpImageExListener = onUpImageExListener
    }

    fun setOnGenCharExListener(onGenCharExListener: SerialModule.OnGenCharExListener?) {
        this.onGenCharExListener = onGenCharExListener
    }

    fun StopThread(){
        ThreadGenrate(0, true)
    }

    @Synchronized
    private fun ThreadGenrate(count: Int, stop: Boolean = false) {
        Log.d("user", "is thread")

        if (isworking) {
            captureCount = count
            if (captureCount > 4) captureCount = 4
            captureIndex = 0
            isCheckLift = false
            captureTimeout = 0

            /*
			bCancel=true;
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(mDeviceIO==Constants.DEV_IO_USB){
						isworking=true;
						Thread workThread = new Thread(new WorkThread());
			        	workThread.start();
					}else{
						if(mDeviceType==Constants.DEV_8_4G_UART){
							highModule.Cancel(false);
							highModule.FP_GetImage();
							isworking=true;
							PostNclMsg(Constants.FPM_PLACE,Constants.RET_OK);
						}else{
							serialModule.Cancel(false);
							serialModule.FP_GetImage();
							isworking=true;
							PostNclMsg(Constants.FPM_PLACE,Constants.RET_OK);
						}
					}
					captureCount=count;
					if(captureCount>4)
						captureCount=4;
					captureIndex=0;
					isCheckLift=false;
					captureTimeout=0;
				}
			}, 1500);
			*/
        } else {
            handler = null;
            handler = Handler()
            handler?.postDelayed({

                if(stop == true){
                    isworking = false
                }
                if (mDeviceIO == Constants.DEV_IO_USB) {
                    isworking = true
                    workThread = Thread(WorkThread())
                    if(!workThread!!.isAlive){
                        workThread?.start()
                    }

                } else {
                    if (mDeviceType == Constants.DEV_8_4G_UART) {
                        highModule.Cancel(false)
                        highModule.FP_GetImage()
                        isworking = true
                        PostNclMsg(Constants.FPM_PLACE, Constants.RET_OK)
                    } else {
                        serialModule.Cancel(false)
                        serialModule.FP_GetImage()
                        isworking = true
                        PostNclMsg(Constants.FPM_PLACE, Constants.RET_OK)
                    }
                }
                captureCount = count
                if (captureCount > 4) captureCount = 4
                captureIndex = 0
                isCheckLift = false
                captureTimeout = 0
            }, 100)
        }
    }

    fun GenerateTemplate(count: Int): Boolean {
        if (!isopening) return false
        ThreadGenrate(count)
        return true
    }

    fun StartThread(count: Int) : Boolean{
        if (!isopening) return false
        ThreadGenrate(count)
        return true
    }

    fun RestartTask (starter: Boolean) {
        Timer("Reset", false).schedule(3000) {
            isworking = true
            captureCount = 1
            workThread = Thread(WorkThread())
            workThread?.interrupt()
            workThread?.start()
//            if(workThread!!.isAlive){
//                workThread?.start()
//            }

        }

    }

    fun GetTemplateByGen(lptemplate: ByteArray): Int {
        memcpy(lptemplate, 0, refdata, 0, refsize)
        return refsize
    }

    fun GetBmpImage(lpbmpdate: ByteArray): Int {
        return if (m_ImageType == 0) {
            memcpy(lpbmpdate, 0, bmpdata, 0, Constants.STDBMP_SIZE)
            Constants.STDBMP_SIZE
        } else {
            memcpy(lpbmpdate, 0, bmpdata, 0, Constants.RESBMP_SIZE)
            Constants.RESBMP_SIZE
        }
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

    fun SetTimeOut(tm: Int) {
        //timeoutCount=tm*50/30;
        timeoutCount = tm
        if (timeoutCount < 10) timeoutCount = 10
    }


    fun Cancle() {
        if (isworking) {
            bCancel = true
        }
    }

    fun isWorking (): Boolean {
        if(isworking){
            return  true
        }
        else{
            return false
        }

    }

    fun SetLastCheckLift(isLift: Boolean) {
        lastCheckLift = isLift
    }


    companion object {
        const val ACTION_USB_PERMISSION = "com.fgtit.device.USB_PERMISSION"
    }

    init {
        mDeviceType = deviceType
        mDeviceIO = getDeviceIO(mDeviceType)
        mBaudRate = getUartBaudRate(mDeviceType)
        mUartName = getUartName(mDeviceType)
        serialModule = SerialModule()
        usbModule = UsbModule()
        highModule = HighModule()
        SerialModuleInit()
        HighModuleInit()
        Log.i(
            "xpb",
            "Device Info=" + mDeviceType.toString() + "/" + mDeviceIO.toString() +
                    "/" + mBaudRate.toString() + "/" + mUartName
        )
    }
}