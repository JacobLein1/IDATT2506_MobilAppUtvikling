package ntnu.leksjon_07.model

data class Movie(
    val title: String,
    val year: Int,
    val type: String,
    val director: List<String>?,
    val actors: List<String>?,
    val description: String?
)