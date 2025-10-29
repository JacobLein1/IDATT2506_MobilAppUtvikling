package ntnu.leksjon_06.tjener

import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class Server(private val uiView: TextView, private val receivedView: TextView, private val sentView: TextView, private val PORT: Int = 12345) {

	/**
	 * Egendefinert set() som gjør at vi enkelt kan endre teksten som vises i skjermen til
	 * emulatoren med
	 *
	 * ```
	 * ui = "noe"
	 * ```
	 */
	private val io = CoroutineScope(Dispatchers.IO)
	@Volatile private var clientWriter: PrintWriter? = null
	private fun showReceived(msg: String) =
		MainScope().launch { receivedView.append("\n$msg") }

	private fun showSent(msg: String) =
		MainScope().launch { sentView.append("\n$msg") }

	private fun showUi(msg: String) =
		MainScope().launch { uiView.append("\n$msg") }

	@RequiresApi(Build.VERSION_CODES.KITKAT)
    fun start() {
		CoroutineScope(Dispatchers.IO).launch {

			try {
				showUi("Starter Tjener ...")
				// "innapropriate blocking method call" advarsel betyr at tråden
				// stopper helt opp og ikke går til neste linje før denne fullfører, i dette
				// eksempelet er ikke dette så farlig så vi ignorerer advarselen.
				ServerSocket(PORT).use { serverSocket: ServerSocket ->

					showUi("ServerSocket opprettet, venter på at en klient kobler seg til....")

					val clientSocket = serverSocket.accept()
					showUi("En Klient koblet seg til:\n$clientSocket")

					clientWriter = PrintWriter(
						BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream())),
						true
					)
					sendToClient(clientSocket, "Velkommen Klient!")

					readFromClient(clientSocket) // Read continously

				}
			} catch (e: IOException) {
				e.printStackTrace()
				showUi(e.message.toString())
			}
		}
	}

	private fun readFromClient(socket: Socket) {
		val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
		while (true) {
			val message = reader.readLine() ?: break
			showReceived("Klient: $message")
		}
		showUi("Klient koblet fra")
	}

	@RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun sendToClient(socket: Socket, message: String) {
		val w = clientWriter
		if (w == null) {
			showUi("Ingen klient tilkoblet ennå")
		} else {
			w.println(message)   // println legger til \n → readLine() funker på klient
			w.flush()
			showSent("Sendte følgende til klienten:\n$message")
		}
	}
	fun send(message: String) {
		io.launch {
			val w = clientWriter
			if (w == null) {
				showUi("Ingen klient tilkoblet ennå")
			} else {
				w.println(message)   // println -> klientens readLine() fungerer
				w.flush()
				showSent("Sendte følgende til klienten:\n$message")
			}
		}
	}
}
