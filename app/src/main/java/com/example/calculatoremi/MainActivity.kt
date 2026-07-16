package com.example.calculatoremi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.calculatoremi.fragments.HistoryFragment
import com.example.calculatoremi.fragments.HomeFragment
import com.example.calculatoremi.fragments.SettingsFragment
import com.example.calculatoremi.fragments.ToolsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.nav_history -> {
                    replaceFragment(HistoryFragment())
                    true
                }

                R.id.nav_tools -> {
                    replaceFragment(ToolsFragment())
                    true
                }

                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }

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

    // ==========================
    // Dashboard Header Functions
    // ==========================

    fun showHeader() {

        val header = findViewById<View>(R.id.headerLayout)
        val container = findViewById<FragmentContainerView>(R.id.fragmentContainer)

        header.visibility = View.VISIBLE

        val params = container.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin =
            resources.getDimensionPixelSize(R.dimen.dashboard_header_height)
        container.layoutParams = params
    }

    fun hideHeader() {

        val header = findViewById<View>(R.id.headerLayout)
        val container = findViewById<FragmentContainerView>(R.id.fragmentContainer)

        header.visibility = View.GONE

        val params = container.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = 0
        container.layoutParams = params
    }
}