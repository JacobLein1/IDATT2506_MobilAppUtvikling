package com.example.oving3

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FriendAdapter(
    activity: Activity,
    private val items: MutableList<Friend>
) : ArrayAdapter<Friend>(activity, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_friend, parent, false)
        val f = items[position]
        v.findViewById<TextView>(R.id.tvName).text = f.name
        v.findViewById<TextView>(R.id.tvBirth).text = f.birthDate
        return v
    }
}
