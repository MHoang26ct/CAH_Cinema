package com.example.cah_cinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cah_cinema.presentation.auth.ForgotPassword.ForgotPasswordScreen
import com.example.cah_cinema.presentation.auth.login.LoginScreen
import com.example.cah_cinema.presentation.auth.register.RegisterScreen
import com.example.cah_cinema.presentation.detail.MovieDetailScreen
import com.example.cah_cinema.presentation.home.HomeScreen
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CAH_CinemaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onForgotPasswordClick = {
                                    navController.navigate("forgot_password")
                                },
                                onRegisterClick = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterClick = { username, email, password, confirmPassword ->
                                    // Xử lý đăng ký
                                },
                                onLoginClick = {
                                    navController.navigate("login")
                                }
                            )
                        }

                        composable("forgot_password") {
                            ForgotPasswordScreen(
                                onGetNewPasswordClick = { /* Xử lý logic */ }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onMovieClick = { movieId ->
                                    navController.navigate("detail/${movieId}")
                                }
                            )
                        }
                        
                        composable(
                            route = "detail/{movieId}",
                            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
                        ) {
                            MovieDetailScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
