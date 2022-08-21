package com.sender.views

import android.app.Activity
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sender.R
import com.sender.messages.LatestMessagesActivity
import com.sender.models.ChatMessage
import com.sender.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser: User? =null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latestMessageRow.text=chatMessage.text
        val chatPartnerId:String
        if(chatMessage.fromId== FirebaseAuth.getInstance().uid){
            chatPartnerId=chatMessage.toId
        }else{
            chatPartnerId=chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                if (chatPartnerUser != null) {
                    viewHolder.itemView.username_textview_latestMessageRow.text= chatPartnerUser!!.username
                    //Picasso.get().load(chatPartnerUser!!.profileImageUrl).into(viewHolder.itemView.imageView_latestMessage)
                    Glide.with(viewHolder.itemView.imageView_latestMessage).load(chatPartnerUser!!.profileImageUrl).into(viewHolder.itemView.imageView_latestMessage)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}