package com.xcodelabs.postra.Notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService()
{
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshtToken = FirebaseInstanceId.getInstance().token

        if (firebaseUser!= null)
        {
            updateToken(refreshtToken)
        }
    }

    private fun updateToken(refreshtToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token = Token(refreshtToken!!)
        ref.child(firebaseUser!!.uid).setValue(token)
    }
}