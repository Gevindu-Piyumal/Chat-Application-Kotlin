package com.sender.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sender.R
import com.sender.models.ChatMessage
import com.sender.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    private lateinit var chatLogRecyclerView : RecyclerView
    private lateinit var typingEditTextChatLog: EditText
    private lateinit var sendButtonChatLog: Button

    companion object{
        val TAG = "debugChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var fromUser:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        chatLogRecyclerView=findViewById(R.id.recyclerview_chatlog)
        typingEditTextChatLog=findViewById(R.id.typing_eddittext_chatlog)
        sendButtonChatLog=findViewById(R.id.send_button_chatlog)

        chatLogRecyclerView.adapter=adapter

        fromUser = intent.getParcelableExtra<User>("USER_KEY")

        if (fromUser != null) {
            supportActionBar?.title=fromUser?.username
        }

        listenForMessages()

        sendButtonChatLog.setOnClickListener {
            Log.d(TAG,"Attempt to send message")
            performSendMessage()
        }
    }



    private fun listenForMessages(){
        val fromId=FirebaseAuth.getInstance().uid
        val toId= fromUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG,chatMessage.text)

                    if(chatMessage.fromId==FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatToItem(chatMessage.text, currentUser!!))

                    }
                    else{
                        adapter.add(ChatFromItem(chatMessage.text,fromUser!!))
                    }
                }
                chatLogRecyclerView.scrollToPosition(adapter.itemCount -1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun performSendMessage(){
        val text = typingEditTextChatLog.text.toString()
        val fromId=FirebaseAuth.getInstance().uid

        val toId= fromUser?.uid

        if(fromId ==null) return
        if(toId ==null) return

        val reference= FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference= FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${reference.key}")
                typingEditTextChatLog.text.clear()
                chatLogRecyclerView.scrollToPosition(adapter.itemCount -1)
            }
        toReference.setValue(chatMessage)

        val latestMessageFromReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageFromReference.setValue(chatMessage)

        val latestMessageToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToReference.setValue(chatMessage)
    }
}

class ChatFromItem(val text:String,val user: User):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_fromRow.text=text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_fromRow
        //Picasso.get().load(uri).into(targetImageView)
        Glide.with(targetImageView).load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text:String, val user:User):Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_toRow.text=text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_toRow
        //Picasso.get().load(uri).into(targetImageView)
        Glide.with(targetImageView).load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}

