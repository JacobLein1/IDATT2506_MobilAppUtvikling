package ntnu.leksjon_06.tjener

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi

class MainActivity : Activity() {
	private lateinit var server: Server

	@RequiresApi(Build.VERSION_CODES.KITKAT)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val uiView = findViewById<TextView>(R.id.systemMessages)
		val received = findViewById<TextView>(R.id.receivedMessages)
		val sent = findViewById<TextView>(R.id.sentMessages)

		val input = findViewById<EditText>(R.id.inputMessage)
		val sendButton = findViewById<Button>(R.id.sendButton)

		server = Server(uiView = uiView, receivedView = received, sentView = sent)
		server.start()

		sendButton.setOnClickListener {
			val msg = input.text.toString().trim()
			if (msg.isNotEmpty()) {
				server.send(msg)
				input.text.clear()
			}
		}
	}
}
