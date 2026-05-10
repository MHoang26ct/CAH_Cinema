package com.example.cah_cinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cah_cinema.presentation.auth.ForgotPassword.ForgotPasswordScreen
import com.example.cah_cinema.presentation.auth.login.LoginScreen
import com.example.cah_cinema.presentation.auth.register.RegisterScreen
import com.example.cah_cinema.presentation.booking.ConcessionScreen
import com.example.cah_cinema.presentation.booking.PaymentScreen
import com.example.cah_cinema.presentation.booking.SeatSelectionScreen
import com.example.cah_cinema.presentation.booking.TicketSelectionScreen
import com.example.cah_cinema.presentation.cinema.CinemaDetailScreen
import com.example.cah_cinema.presentation.cinema.CinemaScreen
import com.example.cah_cinema.presentation.detail.MovieDetailScreen
import com.example.cah_cinema.presentation.home.HomeScreen
import com.example.cah_cinema.presentation.navigation.BottomNavigationBar
import com.example.cah_cinema.presentation.navigation.NotificationScreen
import com.example.cah_cinema.presentation.profile.ProfileScreen
import com.example.cah_cinema.presentation.profile.ProfileViewModel
import com.example.cah_cinema.presentation.profile.TicketInfo
import com.example.cah_cinema.presentation.booking.PaymentViewModel
import com.example.cah_cinema.presentation.profile.ChangePasswordScreen
import com.example.cah_cinema.presentation.profile.EditProfileScreen
import com.example.cah_cinema.presentation.profile.TicketDetailScreen
import com.example.cah_cinema.presentation.booking.PaymentLoadingScreen
import com.example.cah_cinema.presentation.home.UpcomingMoviesScreen
import com.example.cah_cinema.presentation.splash.SplashScreen
import com.example.cah_cinema.presentation.profile.ProfileEvent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.presentation.navigation.Screen
import com.example.cah_cinema.presentation.promotion.PromotionDetailScreen
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CAH_CinemaTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // List of screens where bottom bar should be visible
                val bottomBarScreens = listOf(
                    Screen.Home.route,
                    Screen.Cinema.route,
                    Screen.Notification.route,
                    Screen.Profile.route
                )

                Scaffold(
                    containerColor = Color(0xFF13131A),
                    bottomBar = {
                        if (currentRoute in bottomBarScreens) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onHomeClick = { navController.navigateToTab(Screen.Home.route) },
                                onCinemaClick = { navController.navigateToTab(Screen.Cinema.route) },
                                onNotificationClick = { navController.navigateToTab(Screen.Notification.route) },
                                onProfileClick = { navController.navigateToTab(Screen.Profile.route) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (currentRoute in bottomBarScreens) innerPadding.calculateBottomPadding() else 0.dp),
                        color = Color(0xFF13131A),
                    ) {
                        // Define tab indices for directional animation
                        val tabIndices = mapOf(
                            Screen.Home.route to 0,
                            Screen.Cinema.route to 1,
                            Screen.Notification.route to 2,
                            Screen.Profile.route to 3
                        )

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route,
                            modifier = Modifier.fillMaxSize(),
                            enterTransition = {
                                val initial = initialState.destination.route ?: ""
                                val target = targetState.destination.route ?: ""
                                
                                val initialIndex = tabIndices[initial]
                                val targetIndex = tabIndices[target]

                                if (initialIndex != null && targetIndex != null) {
                                    if (targetIndex > initialIndex) {
                                        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                    } else {
                                        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                    }
                                } else {
                                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                }
                            },
                            exitTransition = {
                                val initial = initialState.destination.route ?: ""
                                val target = targetState.destination.route ?: ""
                                
                                val initialIndex = tabIndices[initial]
                                val targetIndex = tabIndices[target]

                                if (initialIndex != null && targetIndex != null) {
                                    if (targetIndex > initialIndex) {
                                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                    } else {
                                        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                    }
                                } else {
                                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                }
                            },
                            popEnterTransition = {
                                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                            },
                            popExitTransition = {
                                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                            }
                        ) {
                            composable(Screen.Splash.route) {
                                SplashScreen(
                                    onNextScreen = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Login.route) {
                                LoginScreen(
                                    onLoginSuccess = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    },
                                    onForgotPasswordClick = {
                                        navController.navigate(Screen.ForgotPassword.route)
                                    },
                                    onRegisterClick = {
                                        navController.navigate(Screen.Register.route)
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Register.route) {
                                RegisterScreen(
                                    onLoginClick = {
                                        navController.navigate(Screen.Login.route)
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.ForgotPassword.route) {
                                ForgotPasswordScreen(
                                    onGetNewPasswordClick = { /* Logic */ }
                                )
                            }

                            composable(Screen.Home.route) {
                                HomeScreen(
                                    onMovieClick = { movieId ->
                                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                                    },
                                    onPromotionClick = { promotionId ->
                                        navController.navigate(Screen.PromotionDetail.createRoute(promotionId))
                                    },
                                    onSeeAllUpcomingClick = {
                                        navController.navigate(Screen.UpcomingMovies.route)
                                    },
                                    onSeeAllPromotionsClick = {
                                        navController.navigateToTab(Screen.Notification.route)
                                    }
                                )
                            }

                            composable(Screen.UpcomingMovies.route) {
                                UpcomingMoviesScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onMovieClick = { movieId ->
                                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Cinema.route) {
                                CinemaScreen(
                                    onCinemaClick = { cinemaId ->
                                        navController.navigate(Screen.CinemaDetail.createRoute(cinemaId))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Notification.route) {
                                NotificationScreen(
                                    onPromotionClick = { promotionId ->
                                        navController.navigate(Screen.PromotionDetail.createRoute(promotionId))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Profile.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onNavigateToChangePassword = {
                                        navController.navigate(Screen.ChangePassword.route)
                                    },
                                    onNavigateToEditProfile = {
                                        navController.navigate(Screen.EditProfile.route)
                                    },
                                    onNavigateToTicketDetail = {
                                        navController.navigate(Screen.TicketDetail.route)
                                    },
                                    onLogout = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.ChangePassword.route) {
                                ChangePasswordScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onSaveClick = { _, _, _ ->
                                        // Handle save logic
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.EditProfile.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                EditProfileScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onSaveClick = { _, _, _ ->
                                        // Handle save logic
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                            
                            composable(
                                route = Screen.CinemaDetail.route,
                                arguments = listOf(navArgument("cinemaId") { type = NavType.StringType })
                            ) {
                                CinemaDetailScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onShowtimeClick = { movieId, showtimeId, date, time ->
                                        val encodedDate = date.replace("/", "-")
                                        navController.navigate(Screen.TicketSelection.createRoute(movieId, showtimeId, encodedDate, time))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = Screen.PromotionDetail.route,
                                arguments = listOf(navArgument("promotionId") { type = NavType.StringType })
                            ) {
                                PromotionDetailScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = Screen.MovieDetail.route,
                                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
                            ) {
                                MovieDetailScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onShowtimeClick = { movieId, showtimeId, date, time ->
                                        val encodedDate = date.replace("/", "-")
                                        navController.navigate(Screen.TicketSelection.createRoute(movieId, showtimeId, encodedDate, time))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = Screen.TicketSelection.route,
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
                                        navController.navigate(Screen.SeatSelection.createRoute(movieId, showtimeId, date, time, regularCount, coupleCount, basePrice.toLong()))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = Screen.SeatSelection.route,
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
                                        navController.navigate(
                                            Screen.Concession.createRoute(
                                                seats,
                                                totalAmount.toFloat(),
                                                date,
                                                time,
                                            ),
                                        )
                                    },
                                )
                            }

                            composable(
                                route = Screen.Concession.route,
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
                                        navController.navigate(Screen.Payment.createRoute(seats, updatedTotal.toFloat(), date, time))
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = Screen.Payment.route,
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
                                        navController.navigate(Screen.PaymentLoading.route) {
                                            popUpTo(Screen.Home.route) { inclusive = false }
                                        }
                                    }
                                )
                            }

                            composable(Screen.PaymentLoading.route) {
                                val profileViewModel: ProfileViewModel = viewModel()
                                val paymentViewModel: PaymentViewModel = viewModel()
                                val paymentState by paymentViewModel.uiState.collectAsState()
                                
                                PaymentLoadingScreen(
                                    onLoadingComplete = {
                                        // Update ProfileState with the new ticket info before navigating
                                        profileViewModel.updateRecentTicket(
                                            TicketInfo(
                                                movieTitle = paymentState.movieTitle,
                                                cinemaName = paymentState.cinemaName,
                                                showTime = "${paymentState.showtime} - ${paymentState.date}",
                                                seat = paymentState.selectedSeats.joinToString(", "),
                                                posterUrl = paymentState.posterUrl
                                            )
                                        )

                                        navController.navigate(Screen.TicketDetail.route) {
                                            popUpTo(Screen.PaymentLoading.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.TicketDetail.route) {
                                val viewModel: ProfileViewModel = viewModel()
                                TicketDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Extension function to handle bottom navigation tab switching properly.
 * - Reuses existing screen instances if possible.
 * - Prevents building up a large backstack.
 * - Restores state when returning to a tab.
 */
fun NavController.navigateToTab(route: String) {
    this.navigate(route) {
        // Pop up to the start destination of the graph to avoid building up a large stack
        popUpTo(this@navigateToTab.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}
