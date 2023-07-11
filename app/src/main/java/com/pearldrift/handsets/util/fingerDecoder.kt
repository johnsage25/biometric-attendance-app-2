package com.pearldrift.handsets.util

import android.util.Base64

object fingerDecoder {

    fun BytesToBase64(ba: ByteArray?): String {
        return Base64.encodeToString(ba, Base64.DEFAULT)
    }

    fun Base64ToBytes(txt: String?): ByteArray {
        return Base64.decode(txt, Base64.DEFAULT)
    }

}