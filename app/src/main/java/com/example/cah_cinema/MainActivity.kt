package com.example.cah_cinema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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
import com.example.cah_cinema.presentation.user.auth.ForgotPassword.ForgotPasswordScreen
import com.example.cah_cinema.presentation.user.auth.ForgotPassword.OtpVerificationScreen
import com.example.cah_cinema.presentation.user.auth.ForgotPassword.ResetPasswordScreen
import com.example.cah_cinema.presentation.user.auth.login.LoginScreen
import com.example.cah_cinema.presentation.user.auth.register.RegisterScreen
import com.example.cah_cinema.presentation.user.booking.ConcessionScreen
import com.example.cah_cinema.presentation.user.booking.PaymentScreen
import com.example.cah_cinema.presentation.user.booking.SeatSelectionScreen
import com.example.cah_cinema.presentation.user.booking.TicketSelectionScreen
import com.example.cah_cinema.presentation.user.cinema.CinemaDetailScreen
import com.example.cah_cinema.presentation.user.cinema.CinemaScreen
import com.example.cah_cinema.presentation.user.detail.MovieDetailScreen
import com.example.cah_cinema.presentation.user.home.HomeScreen
import com.example.cah_cinema.presentation.navigation.BottomNavigationBar
import com.example.cah_cinema.presentation.navigation.NotificationScreen
import com.example.cah_cinema.presentation.user.profile.ProfileScreen
import com.example.cah_cinema.presentation.user.profile.ProfileViewModel
import com.example.cah_cinema.presentation.user.profile.TicketInfo
import com.example.cah_cinema.presentation.user.booking.PaymentViewModel
import com.example.cah_cinema.presentation.user.profile.ChangePasswordScreen
import com.example.cah_cinema.presentation.user.profile.EditProfileScreen
import com.example.cah_cinema.presentation.user.profile.TicketDetailScreen
import com.example.cah_cinema.presentation.user.profile.BookingHistoryScreen
import com.example.cah_cinema.presentation.user.booking.PaymentLoadingScreen
import com.example.cah_cinema.presentation.user.home.UpcomingMoviesScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cah_cinema.presentation.navigation.Screen
import com.example.cah_cinema.presentation.user.booking.VoucherScreen
import com.example.cah_cinema.presentation.admin.dashboard.AdminDashboardScreen
import com.example.cah_cinema.presentation.admin.movies.AdminMovieManagementScreen
import com.example.cah_cinema.presentation.admin.cinema.AdminCinemaManagementScreen
import com.example.cah_cinema.presentation.admin.voucher.AdminVoucherScreen
import com.example.cah_cinema.presentation.admin.showtime.AdminShowtimeScreen
import com.example.cah_cinema.presentation.admin.report.AdminReportScreen
import com.example.cah_cinema.presentation.admin.seats.AdminSeatManagementScreen
import com.example.cah_cinema.presentation.admin.settings.AdminSettingsScreen
import com.example.cah_cinema.presentation.admin.components.AdminSidebar
import com.example.cah_cinema.presentation.user.promotion.PromotionDetailScreen
import com.example.cah_cinema.presentation.main.MainViewModel
import com.example.cah_cinema.ui.theme.CAH_CinemaTheme

import com.example.cah_cinema.data.remote.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        RetrofitClient.init(this)
        
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val isReady by mainViewModel.isReady.collectAsState()
            val startDestination by mainViewModel.startDestination.collectAsState()
            
            splashScreen.setKeepOnScreenCondition { !isReady }

            CAH_CinemaTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                if (!isReady) return@CAH_CinemaTheme

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
                        val tabIndices = mapOf(
                            Screen.Home.route to 0,
                            Screen.Cinema.route to 1,
                            Screen.Notification.route to 2,
                            Screen.Profile.route to 3,
                            Screen.AdminDashboard.route to 10,
                            Screen.AdminMovieManagement.route to 11,
                            Screen.AdminCinemaManagement.route to 12,
                            Screen.AdminShowtimeManagement.route to 13,
                            Screen.AdminFoodManagement.route to 14,
                            Screen.AdminVoucherManagement.route to 15,
                            Screen.AdminReport.route to 16,
                            Screen.AdminSettings.route to 17
                        )

                        val isSidebarExpanded = remember { androidx.compose.runtime.mutableStateOf(false) }

                        Row(modifier = Modifier.fillMaxSize()) {
                            if (currentRoute?.startsWith("admin_") == true) {
                                AdminSidebar(
                                    currentRoute = currentRoute,
                                    isExpanded = isSidebarExpanded.value,
                                    onToggle = { isSidebarExpanded.value = !isSidebarExpanded.value },
                                    onNavigate = { route ->
                                        navController.navigateToTab(route)
                                        isSidebarExpanded.value = false
                                    },
                                    onLogout = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.fillMaxHeight()
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                NavHost(
                                    navController = navController,
                                    startDestination = startDestination,
                                    modifier = Modifier.fillMaxSize(),
                                    enterTransition = {
                                        val initial = initialState.destination.route ?: ""
                                        val target = targetState.destination.route ?: ""
                                        val initialIndex = tabIndices[initial]
                                        val targetIndex = tabIndices[target]
                                        val isAdminTransition = initial.startsWith("admin_") && target.startsWith("admin_")

                                        if (initialIndex != null && targetIndex != null) {
                                            if (isAdminTransition) {
                                                if (targetIndex > initialIndex) {
                                                    slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                                } else {
                                                    slideInVertically(initialOffsetY = { -it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                                }
                                            } else {
                                                if (targetIndex > initialIndex) {
                                                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                                } else {
                                                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                                                }
                                            }
                                        } else {
                                            fadeIn(animationSpec = tween(400))
                                        }
                                    },
                                    exitTransition = {
                                        val initial = initialState.destination.route ?: ""
                                        val target = targetState.destination.route ?: ""
                                        val initialIndex = tabIndices[initial]
                                        val targetIndex = tabIndices[target]
                                        val isAdminTransition = initial.startsWith("admin_") && target.startsWith("admin_")

                                        if (initialIndex != null && targetIndex != null) {
                                            if (isAdminTransition) {
                                                if (targetIndex > initialIndex) {
                                                    slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                                } else {
                                                    slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                                }
                                            } else {
                                                if (targetIndex > initialIndex) {
                                                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                                } else {
                                                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                                                }
                                            }
                                        } else {
                                            fadeOut(animationSpec = tween(400))
                                        }
                                    }
                                ) {
                                    composable(Screen.Login.route) {
                                        LoginScreen(
                                            onLoginSuccess = { role ->
                                                if (role == "ROLE_ADMIN") {
                                                    navController.navigate(Screen.AdminDashboard.route) {
                                                        popUpTo(Screen.Login.route) { inclusive = true }
                                                    }
                                                } else {
                                                    navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.Login.route) { inclusive = true }
                                                    }
                                                }
                                            },
                                            onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                                            onRegisterClick = { navController.navigate(Screen.Register.route) }
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
                                            onLoginClick = { navController.navigate(Screen.Login.route) }
                                        )
                                    }

                                    composable(Screen.ForgotPassword.route) {
                                        ForgotPasswordScreen(
                                            onOtpSent = { email ->
                                                navController.navigate(Screen.OtpVerification.createRoute(email))
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.OtpVerification.route,
                                        arguments = listOf(navArgument("email") { type = NavType.StringType })
                                    ) { entry ->
                                        val email = entry.arguments?.getString("email") ?: ""
                                        OtpVerificationScreen(
                                            email = email,
                                            onOtpVerified = { token ->
                                                val encodedToken = android.net.Uri.encode(token)
                                                navController.navigate(Screen.ResetPassword.createRoute(email, encodedToken))
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.ResetPassword.route,
                                        arguments = listOf(
                                            navArgument("email") { type = NavType.StringType },
                                            navArgument("resetToken") { type = NavType.StringType }
                                        )
                                    ) { entry ->
                                        val email = entry.arguments?.getString("email") ?: ""
                                        val token = entry.arguments?.getString("resetToken") ?: ""
                                        ResetPasswordScreen(
                                            email = email,
                                            resetToken = token,
                                            onResetSuccess = {
                                                navController.navigate(Screen.Login.route) {
                                                    popUpTo(Screen.Login.route) { inclusive = true }
                                                }
                                            }
                                        )
                                    }

                                    composable(Screen.Home.route) {
                                        HomeScreen(
                                            onMovieClick = { movieId -> navController.navigate(Screen.MovieDetail.createRoute(movieId)) },
                                            onPromotionClick = { promotionId -> navController.navigate(Screen.PromotionDetail.createRoute(promotionId)) },
                                            onSeeAllUpcomingClick = { navController.navigate(Screen.UpcomingMovies.route) },
                                            onSeeAllPromotionsClick = { navController.navigateToTab(Screen.Notification.route) }
                                        )
                                    }

                                    composable(Screen.UpcomingMovies.route) {
                                        UpcomingMoviesScreen(
                                            onBackClick = { navController.popBackStack() },
                                            onMovieClick = { movieId -> navController.navigate(Screen.MovieDetail.createRoute(movieId)) }
                                        )
                                    }

                                    composable(Screen.Cinema.route) {
                                        CinemaScreen(
                                            onCinemaClick = { cinemaId -> navController.navigate(Screen.CinemaDetail.createRoute(cinemaId)) }
                                        )
                                    }

                                    composable(Screen.Notification.route) {
                                        NotificationScreen(
                                            onPromotionClick = { promotionId -> navController.navigate(Screen.PromotionDetail.createRoute(promotionId)) }
                                        )
                                    }

                                    composable(Screen.Profile.route) {
                                        val viewModel: ProfileViewModel = viewModel()
                                        ProfileScreen(
                                            viewModel = viewModel,
                                            onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
                                            onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                                            onNavigateToAllTickets = { navController.navigate(Screen.BookingHistory.route) },
                                            onNavigateToTicketDetail = { navController.navigate(Screen.TicketDetail.route) },
                                            onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                                            onLogout = {
                                                navController.navigate(Screen.Login.route) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        )
                                    }

                                    composable(Screen.BookingHistory.route) {
                                        val viewModel: ProfileViewModel = viewModel()
                                        BookingHistoryScreen(
                                            viewModel = viewModel,
                                            onBackClick = { navController.popBackStack() },
                                            onTicketClick = { invoice ->
                                                viewModel.setSelectedInvoice(invoice)
                                                navController.navigate(Screen.TicketDetail.route)
                                            }
                                        )
                                    }

                                    composable(Screen.ChangePassword.route) {
                                        ChangePasswordScreen(
                                            onBackClick = { navController.popBackStack() },
                                            onSaveClick = { _, _, _ -> navController.popBackStack() }
                                        )
                                    }

                                    composable(Screen.EditProfile.route) {
                                        val viewModel: ProfileViewModel = viewModel()
                                        EditProfileScreen(
                                            viewModel = viewModel,
                                            onBackClick = { navController.popBackStack() },
                                            onSaveClick = { _, _, _ -> navController.popBackStack() }
                                        )
                                    }
                                    
                                    composable(
                                        route = Screen.CinemaDetail.route,
                                        arguments = listOf(navArgument("cinemaId") { type = NavType.StringType })
                                    ) {
                                        CinemaDetailScreen(
                                            onBackClick = { navController.popBackStack() },
                                            onShowtimeClick = { movieId, showtimeId, date, time ->
                                                val encodedDate = date.replace("/", "-")
                                                navController.navigate(Screen.TicketSelection.createRoute(movieId, showtimeId, encodedDate, time))
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.PromotionDetail.route,
                                        arguments = listOf(navArgument("promotionId") { type = NavType.StringType })
                                    ) {
                                        PromotionDetailScreen(onBackClick = { navController.popBackStack() })
                                    }

                                    composable(
                                        route = Screen.MovieDetail.route,
                                        arguments = listOf(navArgument("movieId") { type = NavType.StringType })
                                    ) {
                                        MovieDetailScreen(
                                            onBackClick = { navController.popBackStack() },
                                            onShowtimeClick = { movieId, showtimeId, date, time ->
                                                val encodedDate = date.replace("/", "-")
                                                navController.navigate(Screen.TicketSelection.createRoute(movieId, showtimeId, encodedDate, time))
                                            }
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
                                            onBackClick = { navController.popBackStack() },
                                            onBookClick = { regularCount, coupleCount, basePrice ->
                                                val movieId = entry.arguments?.getString("movieId") ?: ""
                                                val showtimeId = entry.arguments?.getString("showtimeId") ?: ""
                                                val date = entry.arguments?.getString("date") ?: ""
                                                val time = entry.arguments?.getString("time") ?: ""
                                                navController.navigate(Screen.SeatSelection.createRoute(movieId, showtimeId, date, time, regularCount, coupleCount, basePrice.toLong()))
                                            }
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
                                        val movieId = entry.arguments?.getString("movieId") ?: ""
                                        val showtimeId = entry.arguments?.getString("showtimeId") ?: ""
                                        val date = entry.arguments?.getString("date") ?: ""
                                        val time = entry.arguments?.getString("time") ?: ""
                                        SeatSelectionScreen(
                                            onBackClick = { navController.popBackStack() },
                                            onConfirmClick = { seatIds, seatsDisplay, totalAmount ->
                                                navController.navigate(
                                                    Screen.Concession.createRoute(movieId, showtimeId, seatIds, seatsDisplay, totalAmount.toFloat(), date, time)
                                                )
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.Concession.route,
                                        arguments = listOf(
                                            navArgument("movieId") { type = NavType.StringType },
                                            navArgument("showtimeId") { type = NavType.StringType },
                                            navArgument("seatIds") { type = NavType.StringType },
                                            navArgument("seatsDisplay") { type = NavType.StringType },
                                            navArgument("totalAmount") { type = NavType.FloatType },
                                            navArgument("date") { type = NavType.StringType },
                                            navArgument("time") { type = NavType.StringType }
                                        )
                                    ) { entry ->
                                        val movieId = entry.arguments?.getString("movieId") ?: ""
                                        val showtimeId = entry.arguments?.getString("showtimeId") ?: ""
                                        val seatIds = entry.arguments?.getString("seatIds") ?: ""
                                        val seatsDisplay = entry.arguments?.getString("seatsDisplay") ?: ""
                                        val date = entry.arguments?.getString("date") ?: ""
                                        val time = entry.arguments?.getString("time") ?: ""
                                        val paymentViewModel: PaymentViewModel = viewModel(viewModelStoreOwner = entry)
                                        ConcessionScreen(
                                            paymentViewModel = paymentViewModel,
                                            onBackClick = { navController.popBackStack() },
                                            onPaymentClick = { updatedTotal ->
                                                navController.navigate(
                                                    Screen.Payment.createRoute(movieId, showtimeId, seatIds, seatsDisplay, updatedTotal.toFloat(), date, time)
                                                )
                                            }
                                        )
                                    }

                                    composable(
                                        route = Screen.Payment.route,
                                        arguments = listOf(
                                            navArgument("movieId") { type = NavType.StringType },
                                            navArgument("showtimeId") { type = NavType.StringType },
                                            navArgument("seatIds") { type = NavType.StringType },
                                            navArgument("seatsDisplay") { type = NavType.StringType },
                                            navArgument("totalAmount") { type = NavType.FloatType },
                                            navArgument("date") { type = NavType.StringType },
                                            navArgument("time") { type = NavType.StringType }
                                        )
                                    ) { entry ->
                                        val voucherName by entry.savedStateHandle.getStateFlow<String?>("voucherName", null).collectAsState()
                                        val voucherId by entry.savedStateHandle.getStateFlow<Long?>("voucherId", null).collectAsState()
                                        val voucherDiscount by entry.savedStateHandle.getStateFlow<Double?>("voucherDiscount", null).collectAsState()

                                        val concessionEntry = remember(entry) { navController.getBackStackEntry(Screen.Concession.route) }
                                        val paymentViewModel: PaymentViewModel = viewModel(viewModelStoreOwner = concessionEntry)

                                        PaymentScreen(
                                            viewModel = paymentViewModel,
                                            onBackClick = { navController.popBackStack() },
                                            onPaymentSuccess = { navController.navigate(Screen.PaymentLoading.route) },
                                            onSelectVoucher = { total -> navController.navigate(Screen.Voucher.createRoute(total.toFloat())) },
                                            voucherName = voucherName,
                                            voucherId = voucherId,
                                            voucherDiscount = voucherDiscount
                                        )
                                    }

                                    composable(
                                        route = Screen.Voucher.route,
                                        arguments = listOf(navArgument("totalAmount") { type = NavType.FloatType })
                                    ) { entry ->
                                        val totalAmount = entry.arguments?.getFloat("totalAmount")?.toDouble() ?: 0.0
                                        VoucherScreen(
                                            currentTotal = totalAmount,
                                            onBackClick = { navController.popBackStack() },
                                            onConfirm = { code, id, discount ->
                                                navController.previousBackStackEntry?.savedStateHandle?.set("voucherName", code)
                                                navController.previousBackStackEntry?.savedStateHandle?.set("voucherId", id)
                                                navController.previousBackStackEntry?.savedStateHandle?.set("voucherDiscount", discount)
                                                navController.popBackStack()
                                            }
                                        )
                                    }

                                    composable(Screen.PaymentLoading.route) { entry ->
                                        val profileViewModel: ProfileViewModel = viewModel()
                                        val concessionEntry = remember(entry) { navController.getBackStackEntry(Screen.Concession.route) }
                                        val paymentViewModel: PaymentViewModel = viewModel(viewModelStoreOwner = concessionEntry)
                                        val paymentState by paymentViewModel.uiState.collectAsState()

                                        PaymentLoadingScreen(
                                            onLoadingComplete = {
                                                profileViewModel.updateRecentTicket(
                                                    TicketInfo(
                                                        movieTitle = paymentState.movieTitle,
                                                        cinemaName = paymentState.cinemaName,
                                                        showTime = "${paymentState.showtime} - ${paymentState.date}",
                                                        seat = paymentState.selectedSeats.joinToString(", "),
                                                        posterUrl = paymentState.posterUrl,
                                                        bookingId = paymentState.bookingId ?: 0L
                                                    )
                                                )
                                                profileViewModel.loadProfileData()
                                                navController.navigate(Screen.TicketDetail.route) {
                                                    popUpTo(Screen.Home.route) { inclusive = false }
                                                }
                                            }
                                        )
                                    }

                                    composable(Screen.AdminDashboard.route) { AdminDashboardScreen() }
                                    composable(Screen.AdminMovieManagement.route) { AdminMovieManagementScreen() }
                                    composable(Screen.AdminCinemaManagement.route) { AdminCinemaManagementScreen(onNavigate = { route -> navController.navigate(route) }) }
                                    composable(Screen.AdminVoucherManagement.route) { AdminVoucherScreen() }
                                    composable(Screen.AdminShowtimeManagement.route) { AdminShowtimeScreen(onNavigate = { route -> navController.navigate(route) }) }
                                    composable(Screen.AdminFoodManagement.route) { 
                                        com.example.cah_cinema.presentation.admin.food.AdminFoodManagementScreen() 
                                    }
                                    composable(Screen.AdminReport.route) { AdminReportScreen() }
                                    composable(
                                        route = Screen.AdminSeatManagement.route,
                                        arguments = listOf(navArgument("roomId") { type = NavType.LongType })
                                    ) { entry ->
                                        val roomId = entry.arguments?.getLong("roomId") ?: 0L
                                        AdminSeatManagementScreen(
                                            roomId = roomId,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                    composable(Screen.AdminSettings.route) { AdminSettingsScreen() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun NavController.navigateToTab(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateToTab.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
