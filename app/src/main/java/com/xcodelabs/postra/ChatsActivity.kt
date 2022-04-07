package com.xcodelabs.postra
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.xcodelabs.postra.Model.User
import com.xcodelabs.postra.Notifications.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.xcodelabs.postra.Adapter.ChatListShowAdapter

//this activity use for following chat list this are show following list
class ChatsActivity : AppCompatActivity() {

    var id: String = ""
    var title: String = ""
    private var firebaseUser: FirebaseUser? = null
    var chatListShowAdapter: ChatListShowAdapter? = null
    var userList: List<User>? = null
    var idList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)


        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")


        val toolbar: Toolbar = findViewById(R.id.chats_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view_chatlist)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userList = ArrayList()
        chatListShowAdapter = ChatListShowAdapter(this, userList as ArrayList<User>, false)
        recyclerView.adapter = chatListShowAdapter


        idList = ArrayList()

        updateToken(FirebaseInstanceId.getInstance().token)

        getFollowing()

    }

    private fun updateToken(token: String?)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun getFollowing()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(id!!)
            .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                (idList as ArrayList<String>).clear()

                for (snapshot in p0.children)
                {
                    (idList as ArrayList<String>).add(snapshot.key!!)
                }
                showUsers()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun showUsers()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                (userList as ArrayList<User>).clear()

                for (snapshot in dataSnapshot.children)
                {
                    val user = snapshot.getValue(User::class.java)

                    for (id in idList!!)

                    {
                        if (user!!.getUID() == id)
                        {
                            (userList as ArrayList<User>).add(user!!)
                        }
                    }
                }
                chatListShowAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError)
            {
            }
        })
    }
}