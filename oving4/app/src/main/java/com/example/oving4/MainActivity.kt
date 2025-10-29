package com.example.oving4

import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity(), ListFragment.OnItemSelectedListener {

    private var currentIndex = 0

    private val movies = listOf(
        Movie(
            "Old Boy",
            R.drawable.film2,
            "En mann søker hevn etter å ha vært fanget i 15 år."
        ),
        Movie(
            "Over the Garden Wall",
            R.drawable.film1,
            "To brødre går seg vill i en merkelig, magisk skog."
        ),
        Movie(
            "The Wire",
            R.drawable.film3,
            "Et realistisk blikk på liv og kriminalitet i Baltimore."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnPrevious).setOnClickListener {
            showPrevious()
        }
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            showNext()
        }
        updateDetail()
    }

    private fun updateDetail() {
        val frag = supportFragmentManager
            .findFragmentById(R.id.descriptionFragment) as DescriptionFragment
        frag.updateContent(movies[currentIndex])
    }

    override fun onItemSelected(index: Int) {
        currentIndex = index
        updateDetail()
    }

    private fun showPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            updateDetail()
        }
    }

    private fun showNext() {
        if (currentIndex < movies.lastIndex) {
            currentIndex++
            updateDetail()
        }
    }
}

