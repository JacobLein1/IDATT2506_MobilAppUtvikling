package com.example.oving4

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

// Can't use the version of the lecture because it is deprecated
class DescriptionFragment : Fragment(R.layout.description_fragment) {
    private var imageView: ImageView? = null
    private var textView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        textView = view.findViewById(R.id.textView)
        textView?.text = "Velg en film fra listen"
    }
    @SuppressLint("SetTextI18n")
    fun updateContent(movie: Movie) {
        imageView?.setImageResource(movie.imageRes)
        textView?.text = "${movie.title}\n\n${movie.description}"
    }
}