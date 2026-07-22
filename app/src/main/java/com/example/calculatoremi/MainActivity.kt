package com.example.calculatoremi

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.calculatoremi.fragments.HistoryFragment
import com.example.calculatoremi.fragments.HomeFragment
import com.example.calculatoremi.fragments.SettingsFragment
import com.example.calculatoremi.fragments.ToolsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Only handle bottom nav gesture bar inset here
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav?.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            // 20x Spring Bounce Animation on Selected Tab Icon
            val menuView = bottomNavigation.findViewById<View>(item.itemId)
            menuView?.animate()
                ?.scaleX(0.85f)
                ?.scaleY(0.85f)
                ?.setDuration(80)
                ?.withEndAction {
                    menuView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(OvershootInterpolator(2.5f))
                        .setDuration(180)
                        .start()
                }
                ?.start()

            when (item.itemId) {
                R.id.nav_home -> { replaceFragment(HomeFragment()); true }
                R.id.nav_history -> { replaceFragment(HistoryFragment()); true }
                R.id.nav_tools -> { replaceFragment(ToolsFragment()); true }
                R.id.nav_settings -> { replaceFragment(SettingsFragment()); true }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun showHeader() {
        findViewById<View>(R.id.dashboardHeader)?.visibility = View.VISIBLE
    }

    fun hideHeader() {
        findViewById<View>(R.id.dashboardHeader)?.visibility = View.GONE
    }

    fun showBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val container = findViewById<FragmentContainerView>(R.id.fragmentContainer)
        bottomNav?.visibility = View.VISIBLE
        container?.let {
            val params = it.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = resources.getDimensionPixelSize(R.dimen.bottom_nav_total_space)
            it.layoutParams = params
        }
    }

    fun hideBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val container = findViewById<FragmentContainerView>(R.id.fragmentContainer)
        bottomNav?.visibility = View.GONE
        container?.let {
            val params = it.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 0
            it.layoutParams = params
        }
    }
}