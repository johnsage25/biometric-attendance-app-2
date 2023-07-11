package com.pearldrift.handsets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fgtit.data.StaffLoginDetails
import com.fgtit.data.UserLogin
import com.fgtit.fpcore.ObjectBox
import com.fgtit.fpcore.StaffAuthClass
import com.fgtit.model.Attendance
import com.fgtit.model.StaffDetailData
import com.fgtit.model.User
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.result.Result
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import io.objectbox.Box


class LoginActivity : AppCompatActivity() {

    private  var userBox: Box<StaffDetailData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernamed = findViewById<TextInputLayout>(R.id.username)
        val password = findViewById<TextInputLayout>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val loginError = findViewById<TextView>(R.id.loginError)



        loginButton!!.setOnClickListener {

            val username_text = usernamed.editText?.text.toString()
            val password_text = password.editText?.text.toString()
            if(username_text.isEmpty()){
                usernamed.setError("Username field is required")
            }

            if(password_text.isEmpty()){
                password.setError("Password field is required")
            }

            if(!password_text.isEmpty() && !password_text.isEmpty()){

                FuelManager.instance.basePath = "${getString(R.string.basApi)}"

                val gson = Gson()
                val user = UserLogin(username_text, password_text)
                val json = gson.toJson(user)

                userBox =ObjectBox.store.boxFor(StaffDetailData::class.java)

                Fuel.post("app_auth")
                    .body(json)
                    .header("Content-Type", "application/json")
                    .responseString { _, response, result ->
                        when (result) {
                            is Result.Success -> {
                                val responseBody = result.get()
                                val staff = gson.fromJson(responseBody, StaffLoginDetails::class.java)

                                if(staff.loginStatus){

                                    StaffAuthClass().saveToSharedPreferences("userId", staff.body._id, this@LoginActivity)
                                    StaffAuthClass().saveToSharedPreferences("auth", "true", this@LoginActivity)

                                      with( staff.body ){

                                          var staffData = StaffDetailData(
                                              0,
                                              department= department,
                                              staffAvatar = staffAvatar,
                                              staffid = _id,
                                              fullname= fullname,
                                              email = email,
                                              phone = phone,
                                              username =username,
                                          )

                                          userBox?.put(staffData)
                                      }

                                    val intentMain =  Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intentMain)
                                    finish()

                                }
                                else{
                                    loginError.visibility = View.VISIBLE
                                    Toast.makeText(this@LoginActivity, "Invalid login detail, please check and try again.", Toast.LENGTH_SHORT).show()
                                }



                            }
                            is Result.Failure -> {
                                val error = result.getException()
                                loginError.visibility = View.VISIBLE
                                Toast.makeText(this@LoginActivity, "Invalid login detail, please check and try again.", Toast.LENGTH_SHORT).show()
                                Log.e("user-log", "${error}")

                            }
                        }
                    }


            }


        }


    }
}