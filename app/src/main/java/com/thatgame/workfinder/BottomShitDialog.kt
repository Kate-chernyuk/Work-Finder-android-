package com.thatgame.workfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomShitDialog(private val title: String, private val question: String = ""): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.dialog_response, container, false)
        val titleTextView2: TextView = v.findViewById(R.id.vacancy_title)
        titleTextView2.text = title
        val btn: Button = v.findViewById(R.id.add_cover_letter_button)
        val txt: EditText = v.findViewById(R.id.add_cover_letter_text)
        if (question.isNotEmpty()) {
            txt.visibility = View.VISIBLE
            btn.visibility = View.GONE
            txt.setText(question)
        }
        btn.setOnClickListener {
            btn.visibility = View.GONE
            txt.visibility = View.VISIBLE
        }
        val btn2: Button = v.findViewById(R.id.respond_button)
        btn2.setOnClickListener() {dismiss()}
        return v
    }

}