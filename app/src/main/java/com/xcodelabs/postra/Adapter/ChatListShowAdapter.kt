package com.xcodelabs.postra.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xcodelabs.postra.MessageChatActivity
import com.xcodelabs.postra.Model.Chat
import com.xcodelabs.postra.Model.User
import com.xcodelabs.postra.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatListShowAdapter(mContext: Context, mUsers: List<User>, isChatCheck: Boolean) : RecyclerView.Adapter<ChatListShowAdapter.ViewHolder?>() {
    private val mContext: Context
    private val mUsers: List<User>
    var lastMsg: String = ""
    private var isChatCheck: Boolean

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListShowAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.my_chat_item_layout, parent, false)
        return ChatListShowAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ChatListShowAdapter.ViewHolder, i: Int) {
        val user: User = mUsers[i]

        holder.userNameTxt.text = user!!.getUsername()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile)
            .into(holder.profieImageView)

        if (isChatCheck) {
            retrieveLastMessage(user.getUID(), holder.lastMessageTxt)
        } else {
            holder.lastMessageTxt.visibility = View.GONE
        }

        if (isChatCheck) {
            if (user.getStatus() == "online") {
                holder.onlineImageView.visibility = View.VISIBLE
                holder.oflineImageView.visibility = View.GONE
            } else {
                holder.onlineImageView.visibility = View.GONE
                holder.oflineImageView.visibility = View.VISIBLE
            }
        } else {
            holder.onlineImageView.visibility = View.GONE
            holder.oflineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user.getUID())
            mContext.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView
        var profieImageView: CircleImageView
        var onlineImageView: CircleImageView
        var oflineImageView: CircleImageView
        var lastMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.username)
            profieImageView = itemView.findViewById(R.id.profile_image_item)
            onlineImageView = itemView.findViewById(R.id.image_online)
            oflineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.massage_last)
        }
    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refrence = FirebaseDatabase.getInstance().reference.child("Chats")

        refrence.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver() == firebaseUser!!.uid &&
                            chat.getSender() == chatUserId ||
                            chat.getReceiver() == chatUserId &&
                            chat.getSender() == firebaseUser!!.uid
                        ) {
                            lastMsg = chat.getMessage()!!
                        }
                    }
                }
                when (lastMsg) {
                    "defaultMsg" -> lastMessageTxt.text = "No Message"
                    "sent you an image." -> lastMessageTxt.text = "image sent"
                    else -> lastMessageTxt.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}