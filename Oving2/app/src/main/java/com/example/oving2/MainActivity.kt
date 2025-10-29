package com.example.oving2

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var firstNum: TextView
    private lateinit var secondNum: TextView
    private lateinit var userAnswer: TextView
    private lateinit var upperLimit: TextView
    private lateinit var randomNumberGenerated: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstNum = findViewById(R.id.text_number1)
        secondNum = findViewById(R.id.text_number2)
        userAnswer = findViewById(R.id.edit_answer)
        upperLimit = findViewById(R.id.edit_upper_limit)

        val button = findViewById<Button>(R.id.button_fetch_random)

        //val intent = Intent("com.example.oving2.createRandomNumber")
        //startActivity(intent)

        button.setOnClickListener {
            val intent = Intent("com.example.oving2.createRandomNumber")
            intent.putExtra("upper_limit", 500)
            startActivityForResult(intent,0)
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }

    fun onClickStartRandomNumber(intent1: Intent, v: Int) {
        val intent = Intent("com.example.oving2.createRandomNumber")
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getIntExtra("random_value", -1)
            if (result != -1) {
                when (requestCode) {
                    0 -> {
                        randomNumberGenerated = findViewById(R.id.textView)
                        randomNumberGenerated.text = "Random number: $result"
                    }
                    1 -> firstNum.text = result.toString()
                    2 -> secondNum.text = result.toString()
                }
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        }

    }

    private fun checkAnswer(isAdd: Boolean){
        val a = firstNum.text.toString().toInt()
        val b = secondNum.text.toString().toInt()
        val expected = if (isAdd) a + b else a*b

        if (userAnswer == null) {
            Toast.makeText(this, "Skriv inn et tall først!", Toast.LENGTH_LONG).show()
            return
        }

        if (userAnswer.text.toString().toInt() == expected) {
            Toast.makeText(this, "Riktig!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Feil, riktig svar er $expected", Toast.LENGTH_LONG).show()
        }
    }

    fun onAddClicked(view: View){
        checkAnswer(true)
        requestNewNumbers()
    }

    fun onMultiplyClicked(view: View){
        checkAnswer(false)
        requestNewNumbers()
    }

    private fun requestNewNumbers() {
        val upper = upperLimit.text.toString().toIntOrNull() ?: 100

        Intent("com.example.oving2.createRandomNumber").apply {
            putExtra("upper_limit", upper)
            startActivityForResult(this, 2)
        }

        Intent("com.example.oving2.createRandomNumber").apply {
            putExtra("upper_limit", upper)
            startActivityForResult(this, 1)
        }
    }
}