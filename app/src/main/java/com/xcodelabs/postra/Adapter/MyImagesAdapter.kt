package com.xcodelabs.postra.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xcodelabs.postra.Fragments.PostDetailsFragment
import com.xcodelabs.postra.Model.Post
import com.xcodelabs.postra.R
import com.squareup.picasso.Picasso
import com.xcodelabs.postra.FullscreenActivity

class MyImagesAdapter(private val mContext: Context, mPost: List<Post>)
    : RecyclerView.Adapter<MyImagesAdapter.ViewHolder?>()
{
    private var mPost: List<Post>? = null



    init {
        this.mPost = mPost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mPost!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost!![position]


        if (post.getPostimage().equals(""))
        {

            holder.postImage.visibility = View.GONE
            holder.postVideo.visibility = View.VISIBLE
            Glide.with(mContext).load(post.getPostvideo()).centerInside().placeholder(R.drawable.profile).into(holder.postVideo)
        }
        else
        {
            Picasso.get().load(post.getPostimage()).into(holder.postImage)

        }

        holder.postImage.setOnClickListener{

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("postId", post.getPostid())

            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }

        holder.postVideo.setOnClickListener {
            val intent = Intent(mContext, FullscreenActivity::class.java)
            intent.putExtra("url", post.getPostvideo())
            intent.putExtra("dec", post.getDescription())
            mContext.startActivity(intent)
        }
    }


    inner class ViewHolder(@NonNull itemView: View)
        : RecyclerView.ViewHolder(itemView)
    {
        var postImage: ImageView
        var postVideo: ImageView

        init {
            postImage = itemView.findViewById(R.id.post_image)
            postVideo = itemView.findViewById(R.id.post_video)
        }
    }
}