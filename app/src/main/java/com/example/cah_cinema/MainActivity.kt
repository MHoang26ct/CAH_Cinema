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
import com.example.cah_cinema.presentation.booking.ConcessionScreen
import com.example.cah_cinema.presentation.booking.PaymentScreen
import com.example.cah_cinema.presentation.booking.SeatSelectionScreen
import com.example.cah_cinema.presentation.booking.TicketSelectionScreen
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
                                },
                                onShowtimeClick = { movieId, showtimeId, date, time ->
                                    val encodedDate = date.replace("/", "-")
                                    navController.navigate("booking/$movieId/$showtimeId/$encodedDate/$time")
                                }
                            )
                        }

                        composable(
                            route = "booking/{movieId}/{showtimeId}/{date}/{time}",
                            arguments = listOf(
                                navArgument("movieId") { type = NavType.StringType },
                                navArgument("showtimeId") { type = NavType.StringType },
                                navArgument("date") { type = NavType.StringType },
                                navArgument("time") { type = NavType.StringType }
                            )
                        ) { entry ->
                            TicketSelectionScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onBookClick = { regularCount, coupleCount, basePrice ->
                                    val movieId = entry.arguments?.getString("movieId") ?: ""
                                    val showtimeId = entry.arguments?.getString("showtimeId") ?: ""
                                    val date = entry.arguments?.getString("date") ?: ""
                                    val time = entry.arguments?.getString("time") ?: ""
                                    navController.navigate("seat_selection/$movieId/$showtimeId/$date/$time/$regularCount/$coupleCount/${basePrice.toLong()}")
                                }
                            )
                        }

                        composable(
                            route = "seat_selection/{movieId}/{showtimeId}/{date}/{time}/{regularCount}/{coupleCount}/{basePrice}",
                            arguments = listOf(
                                navArgument("movieId") { type = NavType.StringType },
                                navArgument("showtimeId") { type = NavType.StringType },
                                navArgument("date") { type = NavType.StringType },
                                navArgument("time") { type = NavType.StringType },
                                navArgument("regularCount") { type = NavType.IntType },
                                navArgument("coupleCount") { type = NavType.IntType },
                                navArgument("basePrice") { type = NavType.LongType }
                            )
                        ) { entry ->
                            val date = entry.arguments?.getString("date") ?: ""
                            val time = entry.arguments?.getString("time") ?: ""
                            SeatSelectionScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onConfirmClick = { seats, totalAmount ->
                                    navController.navigate("concession/$seats/$totalAmount/$date/$time")
                                }
                            )
                        }

                        composable(
                            route = "concession/{seats}/{totalAmount}/{date}/{time}",
                            arguments = listOf(
                                navArgument("seats") { type = NavType.StringType },
                                navArgument("totalAmount") { type = NavType.FloatType },
                                navArgument("date") { type = NavType.StringType },
                                navArgument("time") { type = NavType.StringType }
                            )
                        ) { entry ->
                            val seats = entry.arguments?.getString("seats") ?: ""
                            val date = entry.arguments?.getString("date") ?: ""
                            val time = entry.arguments?.getString("time") ?: ""
                            ConcessionScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onPaymentClick = { updatedTotal ->
                                    navController.navigate("payment/$seats/$updatedTotal/$date/$time")
                                }
                            )
                        }

                        composable(
                            route = "payment/{seats}/{totalAmount}/{date}/{time}",
                            arguments = listOf(
                                navArgument("seats") { type = NavType.StringType },
                                navArgument("totalAmount") { type = NavType.FloatType },
                                navArgument("date") { type = NavType.StringType },
                                navArgument("time") { type = NavType.StringType }
                            )
                        ) {
                            PaymentScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onPaymentSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
