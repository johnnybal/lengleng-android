package com.example.lengleng

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lengleng.ui.theme.LengLengTheme
import com.example.lengleng.views.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LengLengTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginView(navController)
                        }
                        composable("home") {
                            HomeView(navController)
                        }
                        composable("polls") {
                            PollsView(navController)
                        }
                        composable("social") {
                            SocialView(navController)
                        }
                        composable("profile") {
                            ProfileView(navController)
                        }
                        composable("settings") {
                            SettingsView(navController)
                        }
                    }
                }
            }
        }
    }
} 