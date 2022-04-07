package com.xcodelabs.postra

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.xcodelabs.postra.Fragments.HomeFragment
import com.xcodelabs.postra.Fragments.NotificationsFragment
import com.xcodelabs.postra.Fragments.ProfileFragment
import com.xcodelabs.postra.Fragments.SearchFragment


class MainActivity : AppCompatActivity() {

//    lateinit var adView : AdView


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (item.itemId == R.id.nav_home) {
            moveToFragment(HomeFragment())
            return@OnNavigationItemSelectedListener true
        }
        else if (item.itemId == R.id.nav_search) {
            moveToFragment(SearchFragment())
            return@OnNavigationItemSelectedListener true
        }
        else if (item.itemId == R.id.nav_add_post) {
            item.isChecked = false
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Add Photo")
            { dialogInterface, which ->

                val intent = Intent(this, AddPostActivity::class.java)
                startActivity(intent)
                dialogInterface.dismiss()
            }

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Video")
            { dialogInterface, which ->

                val intent = Intent(this, AddVideoActivity::class.java)
                startActivity(intent)
                dialogInterface.dismiss()
            }
            alertDialog.show()
            return@OnNavigationItemSelectedListener true
        }
        else if (item.itemId == R.id.nav_notifications) {
            moveToFragment(NotificationsFragment())
            return@OnNavigationItemSelectedListener true
        }
        else if (item.itemId == R.id.nav_profile) {
            moveToFragment(ProfileFragment())
            return@OnNavigationItemSelectedListener true
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Load an ad into the AdMob banner view.
//        adView = findViewById<View>(R.id.adView) as AdView
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
//
//        adView.adListener = object : AdListener(){
//            override fun onAdFailedToLoad(p0: Int) {
//                super.onAdFailedToLoad(p0)
//            }
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//            }
//            override fun onAdOpened() {
//                super.onAdOpened()
//
//            }
//            override fun onAdClicked() {
//                super.onAdClicked()
//            }
//
//            override fun onAdClosed() {
//                super.onAdClosed()
//            }
//            override fun onAdImpression() {
//                super.onAdImpression()
//            }
//            override fun onAdLeftApplication() {
//                super.onAdLeftApplication()
//            }
//        }
    }


    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}
