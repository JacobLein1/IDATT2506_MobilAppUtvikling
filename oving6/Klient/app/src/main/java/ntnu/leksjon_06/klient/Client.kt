package ntnu.leksjon_06.klient

import android.widget.TextView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class Client(
	private val uiView: TextView,         // systemmeldinger
	private val receivedView: TextView,   // mottatte meldinger
	private val sentView: TextView,       // sendte meldinger
	private val SERVER_IP: String = "10.0.2.2",
	private val SERVER_PORT: Int = 12345
) {
	private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	private fun showUi(msg: String)       = MainScope().launch { uiView.append("\n$msg") }
	private fun showReceived(msg: String) = MainScope().launch { receivedView.append("\n$msg") }
	private fun showSent(msg: String)     = MainScope().launch { sentView.append("\n$msg") }

	@Volatile private var writer: PrintWriter? = null

	fun start() {
		ioScope.launch {
			showUi("Kobler til tjener…")
			try {
				val socket = Socket(SERVER_IP, SERVER_PORT)
				showUi("Koblet til tjener: $socket")

				writer = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)

				ioScope.launch { readFromServer(socket) }

				writer?.println("Hei, jeg er klienten!")
			} catch (e: IOException) {
				showUi("Feil ved tilkobling: ${e.message}")
			}
		}
	}

	private fun readFromServer(socket: Socket) {
		val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
		try {
			while (true) {
				val line = reader.readLine() ?: break
				showReceived("Tjener: $line")
			}
			showUi("Tilkoblingen ble lukket av tjeneren.")
		} catch (e: IOException) {
			showUi("Feil ved mottak: ${e.message}")
		}
	}

	fun send(message: String) {
		ioScope.launch {
			val w = writer
			if (w == null) {
				showUi("Kan ikke sende – ikke tilkoblet tjener")
			} else {
				w.println(message)
				w.flush()
				showSent("Klient: $message")
			}
		}
	}
}
