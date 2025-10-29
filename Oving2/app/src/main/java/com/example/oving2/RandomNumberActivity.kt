package com.example.oving2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

class RandomNumberActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val upperLimit = intent.getIntExtra("upper_limit", 100)
        val value = (0..upperLimit).random()

        val resultIntent = Intent()
        resultIntent.putExtra("random_value", value)
        setResult(Activity.RESULT_OK, resultIntent)

        //Toast.makeText(this, "Random number: $value", Toast.LENGTH_LONG).show()
        finish() // Not the best solution
    }
}
