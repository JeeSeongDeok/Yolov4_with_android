package com.example.gui.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import com.example.gui.R
import androidx.viewpager.widget.ViewPager
import com.example.gui.adapter.Fragment_Pager_Adapter
import devlight.io.library.ntb.NavigationTabBar
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.gui.CalendarView
import com.example.gui.databinding.ActivityMainBinding
import com.example.gui.ui.login.LoginActivity
import java.util.ArrayList

/*
 * MainActivity.java
 * 이 액티비티는 Fragment를 관리함.
 * 다양한 View를 관리할 수 있도록 바텀네비게이션을 사용함.
 * Method - https://github.com/Devlight/NavigationTabBar
 * */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var fragmentManager: FragmentManager? = null
    private var transaction: FragmentTransaction? = null
    var prefs: SharedPreferences? = null
    var calendarView: CalendarView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupUI()
        fragmentManager = supportFragmentManager
        calendarView = CalendarView()
        transaction = fragmentManager!!.beginTransaction()

        // First Run check
        prefs = getSharedPreferences("Pref", MODE_PRIVATE)
        checkFirstRun()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupUI() {
        val viewPager = findViewById<View>(R.id.vp_horizontal_ntb) as ViewPager
        val adapter = Fragment_Pager_Adapter(supportFragmentManager)
        viewPager.adapter = adapter
        val colors = resources.getStringArray(R.array.default_preview)
        val navigationTabBar = findViewById<View>(R.id.ntb_horizontal) as NavigationTabBar
        navigationTabBar.bgColor = Color.parseColor("#14151E")
        navigationTabBar.setIsBadged(false)
        navigationTabBar.setIsTitled(false)
        val models = ArrayList<NavigationTabBar.Model>()
        models.add(
            NavigationTabBar.Model.Builder(
                resources.getDrawable(R.drawable.ic_main_cal),
                Color.parseColor(colors[0])
            )
                .build()
        )
        models.add(
            NavigationTabBar.Model.Builder(
                resources.getDrawable(R.drawable.ic_main_box),
                Color.parseColor(colors[1])
            )
                .build()
        )
        models.add(
            NavigationTabBar.Model.Builder(
                resources.getDrawable(R.drawable.ic_main_main),
                Color.parseColor(colors[2])
            )
                .build()
        )
        models.add(
            NavigationTabBar.Model.Builder(
                resources.getDrawable(R.drawable.ic_main_chart),
                Color.parseColor(colors[3])
            )
                .build()
        )
        models.add(
            NavigationTabBar.Model.Builder(
                resources.getDrawable(R.drawable.ic_main_user),
                Color.parseColor(colors[4])
            )
                .build()
        )
        navigationTabBar.models = models
        navigationTabBar.setViewPager(viewPager, 0)
        navigationTabBar.setIsTinted(true)
        navigationTabBar.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                navigationTabBar.models[position].hideBadge()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        navigationTabBar.postDelayed({
            for (i in navigationTabBar.models.indices) {
                val model = navigationTabBar.models[i]
                navigationTabBar.postDelayed({ model.showBadge() }, (i * 100).toLong())
            }
        }, 500)
    }

    // First Run Function
    fun checkFirstRun() {
        val isFirstRun = prefs!!.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            val newIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(newIntent)
            prefs!!.edit().putBoolean("isFirstRun", false).apply()
        }
    }
}