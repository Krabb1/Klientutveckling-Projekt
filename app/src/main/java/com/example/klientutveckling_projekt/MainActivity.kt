package com.example.klientutveckling_projekt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle

/**
 * MainActivity som hostar bottom navigation drawer
 *
 *
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels()

    private lateinit var backgroundMusicManager: BackgroundMusicManager

    lateinit var repository: ClickRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Detta är koden för bottom drawer
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNav.setupWithNavController(navController)

        repository = ClickRepository(applicationContext)

        backgroundMusicManager = BackgroundMusicManager(applicationContext)

        backgroundMusicManager.playBackgroundMusic()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.musicVolume.collect { volume ->
                    backgroundMusicManager.setVolume(volume)
                }
            }
        }

    }




    /**
     * Överskuggar onStop metoden från Activity
     *
     * Får ClickRepository att spara den tid vid punkten då appen kallar onStop, säkert även om appen kraschar
     */
    override fun onStop() {

        lifecycleScope.launch {
            repository.setLastActiveTime(System.currentTimeMillis())
        }

        backgroundMusicManager.pauseBackgroundMusic()

        super.onStop()
    }

    override fun onDestroy(){
        backgroundMusicManager.stopBackgroundMusic()
        super.onDestroy()
    }
}