package com.ab.sketchflow

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ab.sketchflow.databinding.ActivityMainBinding
import com.ab.sketchflow.util.Utils.showVisibility
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

class MainActivity : AppCompatActivity(),ColorPickerDialogListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        binding.appBarLayout.toolbar.elevation = 0f
    }

    fun showBottomNavigation(show : Boolean){
        binding.navView.showVisibility(show)
    }

    fun showToolBar(show: Boolean){
        binding.appBarLayout.toolbar.showVisibility(show)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        SketchFlowApplication.setSelectedColorCode(color)
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

}