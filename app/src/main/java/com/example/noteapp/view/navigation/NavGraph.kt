package com.example.noteapp.view.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.noteapp.view.AddNote
import com.example.noteapp.view.Home
import com.example.noteapp.view.UpdateNote
import com.example.noteapp.view.main.keepSplashOpened
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimationNav(navHostController: NavHostController) {
    AnimatedNavHost(
        navHostController,
        startDestination = Destination.Home.route
    ) {
        composable(
            route = Destination.Home.route
        ) {
            Home(
                navHostController,
                onDataLoaded = {
                    keepSplashOpened = false
                }
            )
        }
        composable(
            route = Destination.AddNote.route,
            enterTransition = {
                expandIn(
                    expandFrom = Alignment.TopCenter,
                    animationSpec = tween(700, 0, FastOutSlowInEasing)
                )
            },
            exitTransition = {
                shrinkOut(
                    shrinkTowards = Alignment.TopCenter,
                    animationSpec = tween(700, 0, FastOutSlowInEasing)
                )
            }
        ) {
            AddNote(navHostController)
        }
        composable(
            route = Destination.UpdateNote.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
            }
        ) {
            UpdateNote(
                navHostController = navHostController,
                id = it.arguments?.getInt("id")
            )
        }
    }
}