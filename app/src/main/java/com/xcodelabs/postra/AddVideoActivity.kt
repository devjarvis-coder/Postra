package com.xcodelabs.postra

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_add_post.*

import kotlinx.android.synthetic.main.activity_add_video.*

class AddVideoActivity : AppCompatActivity() {

    val VIDEO : Int = 1
    var progressBar: ProgressBar? = null
    private var videoUri: Uri? = null
    private var myUrl = ""
    var storagePostVideoRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)

        storagePostVideoRef = FirebaseStorage.getInstance().reference.child("Posts Videos")

        progressBar = findViewById(R.id.progressBar_main)


        button_upload_main.setOnClickListener(View.OnClickListener {
            UploadVideo()
        })

        chose_video_btn.setOnClickListener(View.OnClickListener {
                view: View? -> val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO)
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VIDEO  &&  resultCode == RESULT_OK  &&  data != null || data!!.data != null)
        {
            videoUri = data.data
            video_view_add.setVideoURI(videoUri)
            val mediaController = MediaController(this)
            video_view_add.setMediaController(mediaController)
            video_view_add.start()
        }

    }

    private fun UploadVideo()
    {
        when
        {
            videoUri == null -> Toast.makeText(this, "Please select video first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(et_video_deception.text.toString()) -> Toast.makeText(this, "please write something...", Toast.LENGTH_LONG).show()


            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("posting...")
                progressDialog.setMessage("processing...")
                progressDialog.show()
                val fileRef = storagePostVideoRef!!.child(System.currentTimeMillis().toString() + ".mp4")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(videoUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener (OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful)
                        {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = ref.push().key

                            val postMap = HashMap<String, Any>()
                            postMap["postId"] = postId!!
                            postMap["description"] = et_video_deception.text.toString().toLowerCase()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postimage"] = ""
                            postMap["postvideo"] = myUrl

                            ref.child(postId).updateChildren(postMap)

                            Toast.makeText(this, "post successfully.", Toast.LENGTH_LONG).show()

                            val intent = Intent(this@AddVideoActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()
                        }
                        else
                        {
                            progressDialog.dismiss()
                        }
                    } )

            }
        }
    }

//    private fun uploadImage()
//    {
//        when{
//            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
//            TextUtils.isEmpty(et_video_deception.text.toString()) -> Toast.makeText(this, "please write something...", Toast.LENGTH_LONG).show()
//
//            else -> {
//                val progressDialog = ProgressDialog(this)
//                progressDialog.setTitle("Adding New Post")
//                progressDialog.setMessage("Please wait, we are adding your picture post...")
//                progressDialog.show()
//
//                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
//
//                val uploadTask: StorageTask<*>
//                uploadTask = fileRef.putFile(imageUri!!)
//
//                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
//                    if (!task.isSuccessful)
//                    {
//                        task.exception?.let {
//                            throw it
//                            progressDialog.dismiss()
//                        }
//                    }
//                    return@Continuation fileRef.downloadUrl
//                })
//                    .addOnCompleteListener (OnCompleteListener<Uri> { task ->
//                        if (task.isSuccessful)
//                        {
//                            val downloadUrl = task.result
//                            myUrl = downloadUrl.toString()
//
//                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
//                            val postId = ref.push().key
//
//                            val postMap = HashMap<String, Any>()
//                            postMap["postid"] = postId!!
//                            postMap["description"] = description_post.text.toString().toLowerCase()
//                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
//                            postMap["postimage"] = myUrl
//                            postMap["postvideo"] = vdUrl
//
//                            ref.child(postId).updateChildren(postMap)
//
//                            Toast.makeText(this, "Post uploaded successfully.", Toast.LENGTH_LONG).show()
//
//                            val intent = Intent(this@AddVideoActivity, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
//
//                            progressDialog.dismiss()
//                        }
//                        else
//                        {
//                            progressDialog.dismiss()
//                        }
//                    } )
//            }
//        }
//    }

}