package com.example.oving4

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

// Can't use the version of the lecture because it is deprecated
class ListFragment : Fragment(R.layout.list_fragment) {

    interface OnItemSelectedListener{
        fun onItemSelected(index: Int)
    }

    private var listener: OnItemSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnItemSelectedListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listView = view.findViewById<ListView>(R.id.listView)
        val items = listOf("Oldboy", "Over the Garden Wall", "The Wire")
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)

        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onItemSelected(position)
        }
    }

}