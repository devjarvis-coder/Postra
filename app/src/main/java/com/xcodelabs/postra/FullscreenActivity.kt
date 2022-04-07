package com.xcodelabs.postra

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xcodelabs.postra.Adapter.CommentAdapter
import com.xcodelabs.postra.Model.User
import kotlinx.android.synthetic.main.activity_comments.*

class FullscreenActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<com.xcodelabs.postra.Model.Comment>? = null

    private var player: SimpleExoPlayer? = null
    private var playerView: PlayerView? = null
    var textView: TextView? = null
    var fullscreen = false
    var fullscreenButton: ImageView? = null
    private var url: String? = null
    private var playwhenready = false
    private var currentWindow = 0
    private var playbackposition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fullscreen)

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Fullscreen");
//
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

// comment
        firebaseUser = FirebaseAuth.getInstance().currentUser

        playerView = findViewById(R.id.exoplayer_fullscreen)
        textView = findViewById(R.id.tv_fullscreen)


        fullscreenButton = playerView!!.findViewById(R.id.exoplayer_fullscreen_icon)


        val intent = intent
        url = intent.extras!!.getString("url")
        val title = intent.extras!!.getString("dec")

        textView!!.setText(title)

        val recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter


        userInfo()
        readComments()
        getPostImage()


        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment!!.text.toString() == "")
            {
                Toast.makeText(this@FullscreenActivity, "Please write comment first.", Toast.LENGTH_LONG).show()
            }
            else
            {
                addComment()
            }
        })

        fullscreenButton!!.setOnClickListener(View.OnClickListener {
            commentRelative.visibility = View.GONE
            if (fullscreen) {
                fullscreenButton!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@FullscreenActivity,
                        R.drawable.ic_fullscreen_expand
                    )
                )
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val params = playerView!!.getLayoutParams() as RelativeLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = (200 * applicationContext.resources.displayMetrics.density).toInt()
                playerView!!.setLayoutParams(params)
                fullscreen = false
                textView!!.setVisibility(View.VISIBLE)
                commentRelative.visibility = View.VISIBLE
            } else {
                fullscreenButton!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@FullscreenActivity,
                        R.drawable.ic_fullscreen_skrink
                    )
                )
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params = playerView!!.getLayoutParams() as RelativeLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerView!!.setLayoutParams(params)
                fullscreen = true
                textView!!.setVisibility(View.INVISIBLE)
            }
        })
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val datasourcefactory: DataSource.Factory = DefaultHttpDataSourceFactory("video")
        return ProgressiveMediaSource.Factory(datasourcefactory)
            .createMediaSource(uri)
    }

    private fun initializeplayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)
        playerView!!.setPlayer(player)
        val uri = Uri.parse(url)
        val mediaSource = buildMediaSource(uri)
        player!!.setPlayWhenReady(playwhenready)
        player!!.seekTo(currentWindow, playbackposition)
        player!!.prepare(mediaSource, false, false)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 26) {
            initializeplayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT >= 26 || player == null) {
            //  initializeplayer();
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT > 26) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 26) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playwhenready = player!!.playWhenReady
            playbackposition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player = null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player!!.stop()
        releasePlayer()
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    // comment


    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotification()

        add_comment!!.text.clear()
    }


    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_comment)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



    private fun getPostImage()
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId!!).child("postvideo")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val image = p0.value.toString()

//                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comment)
                    Glide.with(this@FullscreenActivity).load(image).centerInside().placeholder(R.drawable.profile).into(post_image_comment)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun readComments()
    {
        val commentsRef = FirebaseDatabase.getInstance()
            .reference.child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    commentList!!.clear()

                    for (snapshot in p0.children)
                    {
                        val comment = snapshot.getValue(com.xcodelabs.postra.Model.Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }



    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(publisherId!!)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "commented: " + add_comment!!.text.toString()
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }
}