package com.example.dzcoffee.dialogs

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.example.dzcoffee.R

class FeedbackDialog(
    ctx: Context,
    private val onSubmit: (Float, String) -> Unit
) : Dialog(ctx) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_feedback)
        setCancelable(true)

        val rating = findViewById<RatingBar>(R.id.ratingBar)
        val comment = findViewById<EditText>(R.id.edtComment)
        val btn = findViewById<Button>(R.id.btnSend)

        btn.setOnClickListener {
            val r = rating.rating
            if (r <= 0f) Toast.makeText(context, "Please rate first", Toast.LENGTH_SHORT).show()
            else { onSubmit(r, comment.text.toString().trim()); dismiss() }
        }
    }
}
