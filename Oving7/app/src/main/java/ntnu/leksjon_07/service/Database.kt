package ntnu.leksjon_07.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ntnu.leksjon_07.managers.DatabaseManager

class Database(context: Context) : DatabaseManager(context) {

	init {
		try {
			this.clear()
			this.insert("Jane Austen", "Pride and Prejudice")
			this.insert("Harper Lee", "To Kill a Mockingbird")
			this.insert("Charles Dickens", "A Tale of Two Cities")
			this.insert("F. Scott Fitzgerald", "The Great Gatsby")
			this.insert("Carl Bernstein", "All The Presidents Men")
			this.insert("Gabriel García Márquez", "One Hundred Years of Solitude")
			this.insert("Charles Dickens", "Great Expectations")
			this.insert("Bob Woodward", "All The Presidents Men")
			this.insert("Charles Dickens", "Oliver Twist")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	val allAuthors: ArrayList<String>
		get() = performQuery(TABLE_AUTHOR, arrayOf(AUTHOR_NAME))

	val allBooks: ArrayList<String>
		get() = performQuery(TABLE_BOOK, arrayOf(ID, BOOK_TITLE), null)

	//Applikasjonen skal la brukeren velge hvilken informasjon som skal vises (f.eks. alle
	//filmer, filmer av en regissør, skuespillere for en film osv.), eksempelkoden er et godt
	//utgangspunkt. Informasjonen skal hentes fra databasen

	val allMovies: ArrayList<String>
		get() = performQuery(TABLE_MOVIE, arrayOf(MOVIE_TITLE))
	val allBooksAndAuthors: ArrayList<String>
		get() {
			val select = arrayOf("$TABLE_BOOK.$BOOK_TITLE", "$TABLE_AUTHOR.$AUTHOR_NAME")
			val from = arrayOf(TABLE_AUTHOR, TABLE_BOOK, TABLE_AUTHOR_BOOK)
			val join = JOIN_AUTHOR_BOOK

			return performRawQuery(select, from, join)
		}

	fun getBooksByAuthor(author: String): ArrayList<String> {
		val select = arrayOf("$TABLE_BOOK.$BOOK_TITLE")
		val from = arrayOf(TABLE_AUTHOR, TABLE_BOOK, TABLE_AUTHOR_BOOK)
		val join = JOIN_AUTHOR_BOOK
		val where = "$TABLE_AUTHOR.$AUTHOR_NAME='$author'"

		return performRawQuery(select, from, join, where)
	}

	fun getMoviesByDirector(director: String): ArrayList<String>{
		val safe = director.replace("'", "''")

		val where = "$MOVIE_DIRECTOR LIKE '%$safe%' COLLATE NOCASE"

		return performQuery(
			table = TABLE_MOVIE,
			columns = arrayOf(MOVIE_TITLE),
			selection = where
		)
	}

	fun getAuthorsByBook(title: String): ArrayList<String> {
		val select = arrayOf("$TABLE_AUTHOR.$AUTHOR_NAME")
		val from = arrayOf(TABLE_AUTHOR, TABLE_BOOK, TABLE_AUTHOR_BOOK)
		val join = JOIN_AUTHOR_BOOK
		val where = "$TABLE_BOOK.$BOOK_TITLE='$title'"

		/*
		You can also just write out the querys manually like below, but this increases the chance of
		spelling mistakes and, creates a lot of work if you want to change names of fields etc.
		later.
		 */
		val query =
			"SELECT AUTHOR.name FROM AUTHOR, BOOK, AUTHOR_BOOK " + "WHERE AUTHOR._id = AUTHOR_BOOK.author_id " + "and BOOK._id = AUTHOR_BOOK.book_id " + "and BOOK.title = '$title'"

		return performRawQuery(select, from, join, where)
	}

	fun getActorsInMovie(title: String): ArrayList<String> {
		val select = arrayOf(MOVIE_ACTORS)
		val where = "$MOVIE_TITLE = '$title'"
		return performQuery(TABLE_MOVIE, select, where)
	}



}
