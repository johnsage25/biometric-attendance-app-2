package com.fgtit.fpcore

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.fgtit.model.StaffDetailData
import com.fgtit.model.StaffDetailData_
import io.objectbox.Box
import io.objectbox.query.QueryBuilder


class StaffAuthClass {

    fun saveToSharedPreferences(key: String, value: String, context: Context) {
        val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun readFromSharedPreferences(key: String, context: Context): String? {
        val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    fun getStaffDetailBox(id: String?): MutableList<StaffDetailData> {
        var userBox = ObjectBox.store.boxFor(StaffDetailData::class.java)
        var data = userBox.query(StaffDetailData_.staffid.equal(id)).build().find()
        return data
    }
}