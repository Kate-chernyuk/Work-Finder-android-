package com.thatgame.workfinder

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Login1Activity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login1)
        emailEditText = findViewById(R.id.emailEditText)
        continueButton = findViewById(R.id.continueButton)


        val clearBtn: ImageView = findViewById(R.id.clearButton)
        val errorText: TextView = findViewById(R.id.errorTextView)

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    continueButton.isEnabled = true
                    emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    emailEditText.background = ColorDrawable(Color.parseColor("#313234"))
                    clearBtn.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                } else {
                    emailEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_email_24, 0, 0, 0)
                    continueButton.isEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        clearBtn.setOnClickListener {
            emailEditText.setText("")
            clearBtn.visibility = View.GONE
        }

        continueButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()) && email.length > 4) {
                val intent = Intent(this@Login1Activity, Login2Activity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                emailEditText.background = ContextCompat.getDrawable(this@Login1Activity, R.drawable.edit_text_background)
                errorText.visibility = View.VISIBLE
            }
        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    true
                }
                R.id.navigation_favorites -> {
                    true
                }
                R.id.navigation_notifications -> {
                    true
                }
                R.id.navigation_messages -> {
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }
    }
}