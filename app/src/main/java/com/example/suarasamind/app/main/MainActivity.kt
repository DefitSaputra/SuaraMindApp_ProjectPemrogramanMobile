package com.example.suarasamind.app.main

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.suarasamind.app.R
import com.example.suarasamind.app.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavWithFadeAnimation()
    }

    private fun setupBottomNavWithFadeAnimation() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            if (item.itemId == navController.currentDestination?.id) {
                return@setOnItemSelectedListener false
            }

            val options = navOptions {
                anim {
                    enter = R.anim.fade_in
                    exit = R.anim.fade_out
                    popEnter = R.anim.fade_in
                    popExit = R.anim.fade_out
                }
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
            }

            navController.navigate(item.itemId, null, options)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavView.menu.findItem(destination.id)?.isChecked = true
        }
    }
}

