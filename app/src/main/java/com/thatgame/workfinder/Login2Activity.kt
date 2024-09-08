package com.thatgame.workfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.text.TextWatcher
import com.google.android.material.bottomnavigation.BottomNavigationView

class Login2Activity : AppCompatActivity() {

    private lateinit var codeEditText1: EditText
    private lateinit var codeEditText2: EditText
    private lateinit var codeEditText3: EditText
    private lateinit var codeEditText4: EditText
    private lateinit var confirmButton: Button
    private lateinit var emailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        codeEditText1 = findViewById(R.id.codeEditText1)
        codeEditText2 = findViewById(R.id.codeEditText2)
        codeEditText3 = findViewById(R.id.codeEditText3)
        codeEditText4 = findViewById(R.id.codeEditText4)
        confirmButton = findViewById(R.id.confirmButton)
        emailTextView = findViewById(R.id.emailTextView)

        val email = intent.getStringExtra("email") ?: "example@mail.ru"
        emailTextView.text = "Отправили код на $email"

        val editTexts = listOf(codeEditText1, codeEditText2, codeEditText3, codeEditText4)
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                    checkCodeCompletion()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        confirmButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    private fun checkCodeCompletion() {
        confirmButton.isEnabled = codeEditText1.text.isNotEmpty() &&
                codeEditText2.text.isNotEmpty() &&
                codeEditText3.text.isNotEmpty() &&
                codeEditText4.text.isNotEmpty()
    }


}