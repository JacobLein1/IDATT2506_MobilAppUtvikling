package ntnu.leksjon_07

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ntnu.leksjon_07.databinding.MinLayoutBinding
import ntnu.leksjon_07.managers.FileManager
import ntnu.leksjon_07.managers.MyPreferenceManager
import ntnu.leksjon_07.service.Database
import java.util.*

class MainActivity : AppCompatActivity() {

	private lateinit var db: Database
	private lateinit var minLayout: MinLayoutBinding
	private lateinit var fileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		minLayout = MinLayoutBinding.inflate(layoutInflater)
		setContentView(minLayout.root)


		db = Database(this)
		MyPreferenceManager(this).updateNightMode()
		fileManager = FileManager(this)


		val movies = fileManager.readMoviesFromRaw(R.raw.movies)
		// Write movies to local file
		fileManager.writeMoviesToLocalFile(movies)

		// Add movies to database
		db.insertMovies(movies)
	}

	private fun showResults(list: ArrayList<String>) {
		val res = StringBuffer("")
		for (s in list) res.append("$s\n")
		minLayout.result.text = res
	}

	private fun showResultsFromRawFile(){
		val jsonFormatted = fileManager.getFormattedMoviesFromRaw(R.raw.movies)
		minLayout.result.text = jsonFormatted
	}
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.settings, menu)
		menu.add(0, 1, 0, "Alle forfattere")
		menu.add(0, 2, 0, "Alle bøker")
		menu.add(0, 3, 0, "Alle bøker og forfattere")
		menu.add(0, 4, 0, "Bøker av Charles Dickens")
		menu.add(0, 5, 0, "Forfattere av \"All the Presidents Men\"")
		menu.add(0,6,0, "Gode filmer/serier")
		menu.add(0,7,0,"Filmer av Christopher Nolan")
		menu.add(0,8,0,"Skuespillere i Old boy")
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.settings -> startActivity(Intent("inft2501.leksjon_07.SettingsActivity").setClassName(packageName,"ntnu.leksjon_07.SettingsActivity"))
			1             -> showResults(db.allAuthors)
			2             -> showResults(db.allBooks)
			3             -> showResults(db.allBooksAndAuthors)
			4             -> showResults(db.getBooksByAuthor("Charles Dickens"))
			5             -> showResults(db.getAuthorsByBook("All The Presidents Men"))
			6 			  -> showResults(db.allMovies)
			7 			  -> showResults(db.getMoviesByDirector("Christopher Nolan"))
			8			  -> showResults(db.getActorsInMovie("Oldboy"))
			else          -> return false
		}
		return super.onOptionsItemSelected(item)
	}

	// Method for background change
	@RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
		super.onResume()
		applyBackgroundColor()
	}

	@RequiresApi(Build.VERSION_CODES.M)
    private fun applyBackgroundColor() {
		val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
		when (prefs.getString("background_color", "blue")) {
			"blue" -> minLayout.root.setBackgroundColor(getColor(R.color.blue))
			"green" -> minLayout.root.setBackgroundColor(getColor(R.color.green))
			"pink" -> minLayout.root.setBackgroundColor(getColor(R.color.pink))
			else -> minLayout.root.setBackgroundColor(getColor(android.R.color.white))
		}
	}

}
