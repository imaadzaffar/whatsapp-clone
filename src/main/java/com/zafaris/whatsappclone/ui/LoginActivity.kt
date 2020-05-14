package com.zafaris.whatsappclone.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.parse.ParseUser
import com.zafaris.whatsappclone.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var loginOrSignup = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (ParseUser.getCurrentUser() != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        title = "WhatsApp Clone: Login"

        button_main.setOnClickListener {
            val username = inputfield_username.editText!!.text.toString()
            val password = inputfield_password.editText!!.text.toString()
            if (loginOrSignup == 0) {
                ParseUser.logInInBackground(username, password) { _, parseException ->
                    if (parseException == null) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, parseException.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val user = ParseUser()
                user.username = username
                user.setPassword(password)
                user.signUpInBackground { parseException ->
                    if (parseException == null) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, parseException.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        button_alt.setOnClickListener {
            if (loginOrSignup == 0) {
                loginOrSignup = 1
                button_main.text = "Sign up"
                textview_alt.text = "Already have an account?"
                button_alt.text = "Login"
            } else {
                loginOrSignup = 0
                button_main.text = "Login"
                textview_alt.text = "Don't have an account?"
                button_alt.text = "Sign up"
            }
        }
    }
}
