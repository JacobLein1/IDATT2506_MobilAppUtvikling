package ntnu.leksjon_05.http

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

const val URL = "https://bigdata.idi.ntnu.no/mobil/ekko.jsp"
const val GAME_URL = "https://bigdata.idi.ntnu.no/mobil/tallspill.jsp"

class MainActivity : AppCompatActivity() {


	private val network: HttpWrapper = HttpWrapper(URL)
	private val api: HttpWrapper = HttpWrapper(GAME_URL)
	//private val network: HttpWrapper = HttpWrapper(URL_JSON)

	private lateinit var etName: EditText
	private lateinit var btnSend: Button
	private lateinit var btnFetchRange: Button
	private lateinit var btnGuess: Button
	private lateinit var etCardNumber: EditText
	private lateinit var tvResultInfo: TextView
	private lateinit var etGuessInput: EditText
	private lateinit var tvResultGuess: TextView


	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		etName = findViewById(R.id.name)
		etCardNumber = findViewById(R.id.cardNumber)
		etGuessInput = findViewById(R.id.guessInput)
		tvResultInfo = findViewById(R.id.result)
		tvResultGuess = findViewById(R.id.guessResult)
		btnSend = findViewById(R.id.btnSend)
		btnFetchRange = findViewById(R.id.btnFetchRange)
		btnGuess = findViewById(R.id.btnGuess)
		findViewById<View>(R.id.InfoSection).visibility = View.VISIBLE
		findViewById<View>(R.id.GuessingSection).visibility = View.VISIBLE


		btnSend.setOnClickListener {
			val params = buildParams() ?: return@setOnClickListener
			performRequest(HTTP.POST, params)

		}
		btnFetchRange.setOnClickListener {
			val params = buildParams() ?: return@setOnClickListener
			startGame(params["navn"]!!, params["kortnummer"]!!)
		}
		btnGuess.setOnClickListener {
			val params = buildGuessParams() ?: return@setOnClickListener
			submitGuess(params)
		}
	}

	private fun buildParams(): Map<String, String>?{
		val name = etName.text.toString().trim()
		val cardNumber = etCardNumber.text.toString().replace(" ","")

		if(name.isEmpty()){
			etName.error = "Skriv inn navn"
			return null
		}
		if(cardNumber.isEmpty() || cardNumber.length != 13 || !cardNumber.all{ it.isDigit() }) {
			etCardNumber.error = "Skriv inn et kortnummer på 13 siffer"
			return null
		}
		return mapOf("navn" to name, "kortnummer" to cardNumber)

	}

	/**
	 *
	 * Build parameters when with guess value
	 *
	 */
	private fun buildGuessParams(): Map<String, String>?{
		val name = etName.text.toString().trim()
		val cardNumber = etCardNumber.text.toString().replace(" ","")
		val guess = etGuessInput.text.toString().trim()

		if(name.isEmpty()){
			etName.error = "Skriv inn navn"
			return null
		}
		if(cardNumber.isEmpty() || cardNumber.length != 13 || !cardNumber.all{ it.isDigit() }) {
			etCardNumber.error = "Skriv inn et kortnummer på 13 siffer"
			return null
		}

		val guessValue = guess.toIntOrNull()
		if (guess.isEmpty() || guessValue == null || guessValue !in 1..10) {
			etGuessInput.error = "Gjett et tall mellom 1 og 10"
			return null
		}
		return mapOf("tall" to guess)
	}
	/**
	 * Utfør en HTTP-forespørsel separat fra hovedtråden
	 */
	private fun performRequest(typeOfRequest: HTTP, parameterList: Map<String, String>) {
		CoroutineScope(IO).launch {
			val response: String = try {
				when (typeOfRequest) {
					HTTP.GET -> network.get(parameterList)
					HTTP.POST -> network.post(parameterList)
					HTTP.GET_WITH_HEADER -> network.getWithHeader(parameterList)
				}
			} catch (e: Exception) {
				Log.e("performRequest()", e.message!!)
				e.toString()
			}

			// Shorten JSON response

			val startMarker = "Parametre fra klient (Felt: Verdi)"

			// Remove header
			var cleanedEcho = response.substringAfter(startMarker, "")

			// Remove null
			cleanedEcho = cleanedEcho
				.substringBeforeLast("null", cleanedEcho)
				.trim()

			val finalResponse = if (cleanedEcho.isEmpty()) {
				// Fallback if failure
				response
			} else {
				"$startMarker\n$cleanedEcho"
			}

			MainScope().launch {
				setResult(finalResponse)
				Toast.makeText(this@MainActivity, "Mottok informasjon", Toast.LENGTH_SHORT).show()
			}
		}
	}

	/**
	 * Function to start game
	 */
	private fun startGame(name: String, cardNumber: String){
		val params = mapOf(
			"navn" to name,
			"kortnummer" to cardNumber
		)
		CoroutineScope(Dispatchers.IO).launch {
			try {
				val response = api.get(params)

				// Main thread
				withContext(Dispatchers.Main) {

					if (response.contains("Oppgi et tall mellom")) {
						findViewById<TextView>(R.id.tipPrompt).text = response
						val m = Regex("""mellom\s+(\d+)\s+og\s+(\d+)""").find(response)
						val prompt = if (m != null) {
							"Gjett et tall mellom ${m.groupValues[1]} og ${m.groupValues[2]}"
						} else {
							"Noe gikk feil"
						}
						findViewById<TextView>(R.id.tipPrompt).text = prompt
					}
				}

			} catch (e: Exception) {
				withContext(Dispatchers.Main) {
					findViewById<TextView>(R.id.result).text =
						"Feil under forespørsel: ${e.message}"
				}
			}
		}
	}

	private fun submitGuess(params: Map<String, String>) {
		setGuessResult("Sender gjetning...")

		CoroutineScope(Dispatchers.IO).launch {
			val response: String = try {
				val result = api.get(params)

				// Legg til en logg for å se rå-responsen fra serveren
				Log.d("Tallspill", "Gjette-respons: $result")
				result
			} catch (e: Exception) {
				Log.e("submitGuess", e.message ?: "Ukjent feil")
				"Feil under forespørsel: ${e.message}"
			}

			withContext(Dispatchers.Main) {
				setGuessResult(response)
				Toast.makeText(this@MainActivity, "Gjettet: ${params["tall"]}", Toast.LENGTH_SHORT).show()
			}
		}
	}

	/**
	 * Show result from server in UI
	 */
	private fun setResult(response: String?) {
		findViewById<TextView>(R.id.result).text = response
	}

	private fun setGuessResult(response: String?) {
		findViewById<TextView>(R.id.guessResult).text = response
	}

	/**
	 * Mange nettsider bruker JSON objekter når de håndterer data, for å konvertere til JSON kan
	 * du bruke
	 * ```kotlin
	 * val json = JSONObject(jsonFormattedString)
	 * ```
	 * Det samme gjelder dersom du mottar en tabell med Json objekter
	 * ```kotlin
	 * val json = JSONObject(jsonFormattedString)
	 * ```
	 * Metoden under konvertere responsesn fra tjeneren til en JSONArray og deretter tilbake til
	 * en formattert streng til å vise i UI, denne metoden brukes ved *GET* forespørsler til
	 * **URL_JSON**.
	 *
	 * @param str må være en en JSONArray-formattert streng f.eks.
	 * ```
	 * [{"a": 1}, {"b", 2}]
	 * ```
	 */
	private fun formatJsonString(str: String): String {
		return try {
			JSONArray(str).toString(4)
		} catch (e: Exception) {
			Log.e("formatJsonString()", e.toString())
			e.message!!
		}
	}
}
