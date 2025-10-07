package com.example.suarasamind.app.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController // Pastikan import ini ada
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.suarasamind.app.R
import com.example.suarasamind.app.auth.LoginActivity
import com.example.suarasamind.app.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val navOptions by lazy {
        navOptions {
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
    }

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.forumFragment, R.id.journalFragment, R.id.profileFragment
            ), binding.drawerLayout // Hubungkan dengan DrawerLayout
        )
        setupBottomNavWithFadeAnimation()
        setupNavigationDrawer()
    }

    private fun setupBottomNavWithFadeAnimation() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            if (item.itemId == navController.currentDestination?.id) {
                return@setOnItemSelectedListener false
            }
            navController.navigate(item.itemId, null, navOptions)
            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavView.menu.findItem(destination.id)?.isChecked = true
        }
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()

            if (menuItem.itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                return@setNavigationItemSelectedListener true
            }

            if (menuItem.itemId == navController.currentDestination?.id) {
                return@setNavigationItemSelectedListener true
            }

            navController.navigate(menuItem.itemId, null, navOptions)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}