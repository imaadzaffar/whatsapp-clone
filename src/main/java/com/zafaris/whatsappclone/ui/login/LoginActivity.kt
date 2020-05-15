package com.zafaris.whatsappclone.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zafaris.whatsappclone.R
import com.zafaris.whatsappclone.model.User
import com.zafaris.whatsappclone.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var prefs: SharedPreferences
    private var loginOrSignup = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "WhatsApp Clone: Login"

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            Toast.makeText(this, "User already logged in", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

        prefs = getSharedPreferences("com.zafaris.whatsappclone", Context.MODE_PRIVATE)

        val userReference = Firebase.database.getReference("users")

        button_main.setOnClickListener {
            val email = inputfield_email.editText!!.text.toString()
            val password = inputfield_password.editText!!.text.toString()
            if (email.isNotEmpty()) {
                if (password.isNotEmpty()) {
                    if (loginOrSignup == 0) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()

                                    userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Toast.makeText(this@LoginActivity, "Error getting user info...", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (userSnapshot in dataSnapshot.children) {
                                                val name = userSnapshot.child("name").getValue<String>()!!
                                                Log.d("name", name)
                                                prefs.edit().putString("name", name).apply()
                                            }
                                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                            startActivity(intent)
                                        }
                                    })

                                } else {
                                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        val name = inputfield_name.editText!!.text.toString()
                        if (name.isNotEmpty()) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = User(name, email, password)
                                        userReference.child(auth.uid!!).setValue(user)
                                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()

                                        prefs.edit().putString("name", name).apply()
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a email", Toast.LENGTH_SHORT).show()
            }
        }

        button_alt.setOnClickListener {
            if (loginOrSignup == 0) {
                loginOrSignup = 1
                inputfield_name.visibility = View.VISIBLE
                button_main.text = "Sign up"
                textview_alt.text = "Already have an account?"
                button_alt.text = "Login"
            } else {
                loginOrSignup = 0
                inputfield_name.visibility = View.GONE
                button_main.text = "Login"
                textview_alt.text = "Don't have an account?"
                button_alt.text = "Sign up"
            }
        }
    }
}
