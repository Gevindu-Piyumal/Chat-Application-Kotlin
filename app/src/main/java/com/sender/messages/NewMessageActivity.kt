package com.sender.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sender.R
import com.sender.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class NewMessageActivity : AppCompatActivity() {

    private lateinit var newMessageRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        newMessageRecyclerView = findViewById(R.id.recyclerView_newMessage)

        supportActionBar?.title = "Select User"

        val adapter = GroupAdapter<ViewHolder>()

        newMessageRecyclerView.adapter=adapter
        fetchUsers()
    }


    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    Log.d("debug",it.toString())
                    val user = it.getValue(User::class.java)
                    if(user!=null){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra("USER_KEY",userItem.user)
                    startActivity(intent)
                    finish()
                }
                newMessageRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}

class UserItem(val user:User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var usernameUserRow:TextView = viewHolder.itemView.findViewById(R.id.username_textview_userRowNewMessage)
        var profilePicUserRow:CircleImageView = viewHolder.itemView.findViewById(R.id.profilepic_imageview_userRowNewMessage)
        usernameUserRow.text=user.username
        Picasso.get().load(user.profileImageUrl).into(profilePicUserRow)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}
