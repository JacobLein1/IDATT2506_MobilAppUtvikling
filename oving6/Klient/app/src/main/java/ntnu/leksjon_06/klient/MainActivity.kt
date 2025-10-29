package ntnu.leksjon_06.klient

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity() {

	private lateinit var client: Client

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val uiView = findViewById<TextView>(R.id.systemMessages)
		val received = findViewById<TextView>(R.id.receivedMessages)
		val sent = findViewById<TextView>(R.id.sentMessages)
		val input = findViewById<EditText>(R.id.inputMessage)
		val sendButton = findViewById<Button>(R.id.sendButton)

		client = Client(uiView, received, sent)

		client.start()

		sendButton.setOnClickListener {
			val msg = input.text.toString().trim()
			if (msg.isNotEmpty()) {
				client.send(msg)
				input.text.clear()
			}
		}
	}
}
