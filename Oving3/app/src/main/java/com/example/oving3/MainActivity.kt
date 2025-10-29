package com.example.oving3

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import com.example.oving3.com.example.oving3.EditFriendActivity


class MainActivity : Activity() {


    private val friends = mutableListOf<Friend>()
    private lateinit var adapter: FriendAdapter

    companion object {
        private const val REQ_ADD = 100
        private const val REQ_EDIT = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<ListView>(R.id.listView)
        adapter = FriendAdapter(this, friends)
        list.adapter = adapter

        // Button for editing friend
        findViewById<Button>(R.id.button_add_friend).setOnClickListener { startActivityForResult(
            Intent(this, EditFriendActivity::class.java), REQ_ADD)
        }

        list.setOnItemClickListener { _, _, pos, _ ->
            val f = friends[pos]
            val i = Intent(this, EditFriendActivity::class.java)
                .putExtra("name", f.name)
                .putExtra("birth", f.birthDate)
                .putExtra("index", pos)
            startActivityForResult(i, REQ_EDIT)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) return

        val name = data.getStringExtra("name") ?: return
        val birth = data.getStringExtra("birth") ?: return
        val index = data.getIntExtra("index", -1)

        when (requestCode) {
            REQ_ADD -> friends.add(Friend(name, birth))
            REQ_EDIT -> if (index in friends.indices) {
                friends[index].name = name
                friends[index].birthDate = birth
            }
        }
        adapter.notifyDataSetChanged()
    }

}
