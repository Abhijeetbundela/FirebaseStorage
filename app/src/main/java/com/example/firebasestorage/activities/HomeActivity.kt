package com.example.firebasestorage.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.firebasestorage.R
import com.example.firebasestorage.fragment.AllPostFragment
import com.example.firebasestorage.fragment.PostFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mPagerAdapter: FragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()

        mPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            private val mFragments =
                arrayOf(
                    AllPostFragment(),
                    PostFragment()
                )
            private val mFragmentNames = arrayOf(
                "All User Posts",
                "My Posts"
            )

            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return mFragmentNames[position]
            }
        }

        view_pager.adapter = mPagerAdapter

        view_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        fab_btn.hide()
                    }
                    1 -> {
                        fab_btn.show()
                    }
                    2 -> {
                        fab_btn.hide()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        tab_layout.setupWithViewPager(view_pager)

        fab_btn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CreatePostActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                mAuth.signOut()
                true
            }

            R.id.user_profile -> {
                startActivity(Intent(this@HomeActivity, UserProfileActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}