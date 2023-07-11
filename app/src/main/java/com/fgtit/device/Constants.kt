package com.fgtit.device

object Constants {
    //Device Define
    const val DEV_IO_UNKNOW = 0x00
    const val DEV_IO_UART = 0x01
    const val DEV_IO_SPI = 0x02
    const val DEV_IO_USB = 0x03

    //
    const val DEV_UNKNOW = 0x00
    const val DEV_5_3G_UART_A4 = 0x10 //Android 4.4.4
    const val DEV_5_3G_UART_340M = 0x13 //Android 6.0 3G 340M
    const val DEV_5_4G_UART_A6 = 0x11 //Android 6.0	35M
    const val DEV_5_3G_UART_A6 = 0x12 //Android 6.0	80M

    //Developing Samples
    //public static final int	DEV_5_3G_UART_A5=0x13;	//Android 5.0	80M	
    //No Support Products
    //public static final int	DEV_5_4G_UART_GT=0x14;	//Android 4.4.4 MSM8916	
    //Access
    const val DEV_5_3G_UART_AC = 0x20 //Android 6.0	80M

    //5寸4G 7.0
    const val DEV_5_4G_UART_AC = 0x21 ///android 7.0

    //Access HF-A5
    const val DEV_5_3G_UART_A5 = 0x22

    //
    const val DEV_6_4G_UART = 0x30

    //
    const val DEV_7_3G_SPI = 0x40 //7' SPI

    //Developing Samples
    const val DEV_7_3G_USB = 0x41
    const val DEV_7_4G_USB = 0x42

    //
    const val DEV_8_WIFI_USB = 0x50 //8' Windows/Android
    const val DEV_8_4G_USB = 0x51 //8' Android USB
    const val DEV_8_4G_UART = 0x52 //8' Android UART
    const val IMAGE_WIDTH = 256
    const val IMAGE_HEIGHT = 288
    const val FPM_DEVICE = 0x01
    const val FPM_PLACE = 0x02
    const val FPM_LIFT = 0x03
    const val FPM_CAPTURE = 0x04
    const val FPM_GENCHAR = 0x05
    const val FPM_ENRFPT = 0x06
    const val FPM_NEWIMAGE = 0x07
    const val FPM_TIMEOUT = 0x08
    const val FPM_IMGVAL = 0x09
    const val RET_OK = 1
    const val RET_FAIL = 0
    const val DEV_FAIL = -2
    const val DEV_NOFOUND = -1
    const val DEV_OK = 0
    const val DEV_ATTACHED = 1
    const val DEV_DETACHED = 2
    const val DEV_CLOSE = 3
    const val STDIMAGE_X = 256
    const val STDIMAGE_Y = 288
    const val RESIMAGE_X = 256
    const val RESIMAGE_Y = 360
    const val STDRAW_SIZE = 73728
    const val STDBMP_SIZE = 74806
    const val RESRAW_SIZE = 92160
    const val RESBMP_SIZE = 93238
    const val TEMPLATESIZE = 256
    const val TIMEOUT_LONG = 0x7FFFFFFF
}