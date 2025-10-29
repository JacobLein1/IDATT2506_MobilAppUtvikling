package com.example.oving3.com.example.oving3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button;
import android.widget.EditText
import com.example.oving3.R

class EditFriendActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val etName = findViewById<EditText>(R.id.etName)
        val etBirth = findViewById<EditText>(R.id.etBirth)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val birth = etBirth.text.toString().trim()

            val result = Intent().apply {
                putExtra("name", name)
                putExtra("birth", birth)

                putExtra("index", intent.getIntExtra("index", -1))
            }
            setResult(RESULT_OK, result)
            finish()
        }
    }
}
