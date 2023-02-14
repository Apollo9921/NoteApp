package com.example.noteapp.view.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.example.noteapp.view.navigation.AnimationNav
import com.example.noteapp.view.theme.NoteAppTheme
import com.example.noteapp.viewModel.NoteViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

var keepSplashOpened = true

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        setContent {
            NoteAppTheme {
                navController = rememberAnimatedNavController()
                AnimationNav(navController)
            }
        }
    }
}