package ntnu.leksjon_07.managers

import ntnu.leksjon_07.model.Movie
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

/**
 * Just contains basic code snippets relevant for reading from/to different files
 */
class FileManager(private val activity: AppCompatActivity) {

	private val filename: String = "movies.json"

	private var dir: File = activity.filesDir
	private var file: File = File(dir, filename)

	private var externalDir: File? = activity.getExternalFilesDir(null)
	private var externalFile = File(externalDir, filename)


	fun write(str: String) {
		PrintWriter(file).use { writer ->
			writer.println(str)
		}
	}

	fun readLine(): String? {
		BufferedReader(FileReader(file)).use { reader ->
			return reader.readLine()
		}
	}

	/**
	 * Open file: *res/raw/id.txt*
	 *
	 * @param fileId R.raw.filename
	 */
    fun readFileFromResFolder(fileId: Int): String {
		val content = StringBuffer("")
		try {
			val inputStream: InputStream = activity.resources.openRawResource(fileId)
			val reader = BufferedReader(InputStreamReader(inputStream)).use { reader ->
				var line = reader.readLine()
				while (line != null) {
					content.append(line)
					content.append("\n")
					line = reader.readLine()
				}

			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return content.toString()
	}

	fun readMoviesFromRaw(fileId: Int): List<Movie>{
		// Read file
		val content = readFileFromResFolder(fileId)
		// Parse to json array
		val arr = JSONArray(content)
		// Create return object
		val out = mutableListOf<Movie>()
		// Loop to go through each JSON-object
		for (i in 0 until arr.length()) {
			// Get each object
			val o: JSONObject = arr.getJSONObject(i)
			// Local help function to get list of strings from json
			fun optStringList(key: String): List<String>? =
				// Check if field exists in json
				if (o.has(key)) {
					// Get value
					val v = o.get(key)
					when (v) {
						// If json is JSONArray, convert every element to list of strings
						is JSONArray -> (0 until v.length()).map { v.getString(it) }
						// If value is single string put it in list
						is String -> listOf(v)
						else -> null
					}
				} else null
			// Create movie object to return
			out += Movie(
				title = o.getString("tittel"),
				year = o.getInt("år"),
				type = o.getString("type"),
				director = optStringList("regissør"),
				actors = optStringList("skuespillere"),
				description = if (o.has("beskrivelse")) o.getString("beskrivelse") else null
			)
		}
		// Return entire list
		return out

	}

	fun getFormattedMoviesFromRaw(fileId: Int): String {
		val content = readFileFromResFolder(fileId)
		val builder = StringBuilder()

		try {
			val array = org.json.JSONArray(content)

			for (i in 0 until array.length()) {
				val obj = array.getJSONObject(i)

				builder.append("${obj.getString("tittel")} (${obj.getInt("år")})\n")
				builder.append("Type: ${obj.getString("type")}\n")

				if (obj.has("regissør"))
					builder.append("Regissør: ${obj.getString("regissør")}\n")

				if (obj.has("beskrivelse"))
					builder.append("${obj.getString("beskrivelse")}\n")

				builder.append("\n────────────────────\n\n")
			}

		} catch (e: Exception) {
			builder.append("Feil ved parsing av JSON: ${e.message}")
		}

		return builder.toString()
	}

	fun writeFile(fileNameLocal: String, content: String){
		val f = File(activity.filesDir, fileNameLocal)
		f.writeText(content)
	}

	private fun moviesToJson(movies: List<Movie>): String {
		val arr = JSONArray()
		movies.forEach { m ->
			val o = JSONObject().apply {
				put("tittel", m.title)
				put("år", m.year)
				put("type", m.type)
				m.director?.let { put("regissør", it) }
				m.actors?.let { put("skuespillere", it) }
				m.description?.let { put("beskrivelse", it) }
			}
			arr.put(o)
		}
		return arr.toString(2) // pretty-print
	}

	fun writeMoviesToLocalFile(movies: List<Movie>) {
		val jsonOut = moviesToJson(movies)
		writeFile("movies_local.json", jsonOut)
	}


}
