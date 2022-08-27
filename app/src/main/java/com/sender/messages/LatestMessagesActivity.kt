package com.sender.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sender.R
import com.sender.RegisterActivity
import com.sender.models.ChatMessage
import com.sender.models.User
import com.sender.registerlogin.LoginActivity
import com.sender.views.LatestMessageRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*


class LatestMessagesActivity : AppCompatActivity(){

    companion object {
        var currentUser: User? = null
    }

    private lateinit var latestMessageRecyclerView: RecyclerView
    private lateinit var fabNewChat:ExtendedFloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        latestMessageTopAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.latest_message_nav_menu_sign_out -> {
                    Log.d("debugLatest", "Current User")
                    signOut()
                    true
                }

                else -> false
            }
        }

        latestMessageRecyclerView = findViewById(R.id.recyclerView_latest_messages)
        fabNewChat = findViewById(R.id.new_chat_fab_latest_message)

        latestMessageRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        latestMessageRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            Log.d("debugLatest", "123")
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra("USER_KEY", row.chatPartnerUser)
            startActivity(intent)
        }

        fabNewChat.setOnClickListener{
            val intent = Intent(this,NewMessageActivity::class.java)
            startActivity(intent)
        }

        fetchCurrentUser()
        verifyUserIsLoggedIn()
        listenForLatestMessages()
    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    //updated refreshRecyclerViewMessages
    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        val sortedMap = HashMap<Long, ChatMessage>()
        //new hashMap

        latestMessagesMap.forEach {
            sortedMap[0 - it.value.timeStamp] = it.value
            //add to new hashMap with 0 - timestamp as key
        }
        sortedMap.toSortedMap().values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    val adapter = GroupAdapter<ViewHolder>()

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("debugLatest", "Current User ${currentUser?.username}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

