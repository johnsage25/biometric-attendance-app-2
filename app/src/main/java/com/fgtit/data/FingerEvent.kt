package com.fgtit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

public var textLottie = MutableLiveData("false")

open class FingerEvent : ViewModel()  {

    private val finger: MutableLiveData<FingerState> by lazy {
        MutableLiveData<FingerState>().also {

        }
    }


    fun setData(animate: String, message: String, data: String){
        finger.value = FingerState(message, animate, data)
    }

    fun getData(): LiveData<FingerState> {
        return  finger
    }
}