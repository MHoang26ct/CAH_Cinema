package com.example.cah_cinema

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.cah_cinema.presentation.admin.promotion.AdminPromotionManagementScreen
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

// Staff imports
import com.example.cah_cinema.presentation.staff.dashboard.StaffDashboardScreen
import com.example.cah_cinema.presentation.staff.dashboard.StaffDashboardViewModel
import com.example.cah_cinema.presentation.staff.checkin.StaffCheckInScreen
import com.example.cah_cinema.presentation.staff.sell.StaffSellTicketScreen
import com.example.cah_cinema.presentation.staff.sell.StaffSellSeatSelectionScreen
import com.example.cah_cinema.presentation.staff.sell.StaffSellPaymentScreen
import com.example.cah_cinema.presentation.staff.sell.StaffPaymentSuccessScreen
import com.example.cah_cinema.presentation.staff.sell.StaffSellTicketViewModel

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

                // Xử lý deep link khi VNPay/MoMo redirect về app
                // URL: cahcinema://payment/result?vnp_ResponseCode=00&...
                LaunchedEffect(intent) {
                    handlePaymentDeepLink(intent, navController)
                }

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
                            Screen.AdminPromotionManagement.route to 13,
                            Screen.AdminShowtimeManagement.route to 14,
                            Screen.AdminFoodManagement.route to 15,
                            Screen.AdminVoucherManagement.route to 16,
                            Screen.AdminReport.route to 17,
                            Screen.AdminSettings.route to 18,
                            Screen.StaffDashboard.route to 20,
                            Screen.StaffCheckIn.route to 21,
                            Screen.StaffSellTicket.route to 22
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
                                                } else if (role == "ROLE_STAFF") {
                                                    navController.navigate(Screen.StaffDashboard.route) {
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
                                        val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
                                        TicketDetailScreen(
                                            viewModel = profileViewModel,
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
                                            onSeeAllPromotionsClick = { navController.navigateToTab(Screen.Notification.route) },
                                            onProfileClick = { navController.navigateToTab(Screen.Profile.route) },
                                            onNotificationClick = { navController.navigateToTab(Screen.Notification.route) }
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
                                        val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
                                        ProfileScreen(
                                            viewModel = profileViewModel,
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
                                        val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
                                        BookingHistoryScreen(
                                            viewModel = profileViewModel,
                                            onBackClick = { navController.popBackStack() },
                                            onTicketClick = { invoice ->
                                                profileViewModel.setSelectedInvoice(invoice)
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
                                        val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
                                        EditProfileScreen(
                                            viewModel = profileViewModel,
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
                                        val profileViewModel: ProfileViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
                                        val concessionEntry = remember(entry) { navController.getBackStackEntry(Screen.Concession.route) }
                                        val paymentViewModel: PaymentViewModel = viewModel(viewModelStoreOwner = concessionEntry)
                                        val paymentState by paymentViewModel.uiState.collectAsState()

                                        PaymentLoadingScreen(
                                            onLoadingComplete = {
                                                val foods = paymentState.concessionSummary.map {
                                                    com.example.cah_cinema.data.model.InvoiceFood(
                                                        foodId = it.foodId,
                                                        foodName = it.name,
                                                        foodImageUrl = it.imageUrl,
                                                        foodCategory = "", // Backend will provide this in history
                                                        quantity = it.quantity,
                                                        unitPrice = it.unitPrice
                                                    )
                                                }
                                                profileViewModel.updateRecentTicket(
                                                    TicketInfo(
                                                        movieTitle = paymentState.movieTitle,
                                                        cinemaName = paymentState.cinemaName,
                                                        showTime = "${paymentState.showtime} - ${paymentState.date}",
                                                        seat = paymentState.selectedSeats.joinToString(", "),
                                                        posterUrl = paymentState.posterUrl,
                                                        bookingId = paymentState.bookingId ?: 0L,
                                                        roomName = paymentState.room,
                                                        totalPrice = paymentState.finalAmount,
                                                        foods = foods,
                                                        discountAmount = paymentState.discount,
                                                        foodTotalPrice = paymentState.concessionTotal
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
                                    composable(Screen.AdminPromotionManagement.route) { AdminPromotionManagementScreen() }
                                    composable(Screen.AdminVoucherManagement.route) { AdminVoucherScreen() }
                                    composable(Screen.AdminShowtimeManagement.route) { AdminShowtimeScreen(onNavigate = { route -> navController.navigate(route) }) }
                                    composable(Screen.AdminFoodManagement.route) { 
                                        com.example.cah_cinema.presentation.admin.food.AdminFoodManagementScreen() 
                                    }
                                    composable(Screen.AdminReport.route) { AdminReportScreen() }
                                    composable(
                                        route = Screen.AdminSeatManagement.route,
                                        arguments = listOf(
                                            navArgument("roomId") { type = NavType.LongType },
                                            navArgument("cinemaId") { type = NavType.LongType }
                                        )
                                    ) { entry ->
                                        val roomId = entry.arguments?.getLong("roomId") ?: 0L
                                        val cinemaId = entry.arguments?.getLong("cinemaId") ?: 0L
                                        AdminSeatManagementScreen(
                                            roomId = roomId,
                                            cinemaId = cinemaId,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                    composable(Screen.AdminSettings.route) { AdminSettingsScreen() }

                                    // ── Staff routes ─────────────────────────────────────────────
                                    composable(Screen.StaffDashboard.route) {
                                        val staffVm: StaffDashboardViewModel = viewModel()
                                        val staffName by staffVm.staffName.collectAsState()
                                        StaffDashboardScreen(
                                            staffName = staffName,
                                            onCheckInClick = {
                                                navController.navigate(Screen.StaffCheckIn.route)
                                            },
                                            onSellTicketClick = {
                                                navController.navigate(Screen.StaffSellTicket.route)
                                            },
                                            onLogoutClick = {
                                                staffVm.logout()
                                                navController.navigate(Screen.Login.route) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            }
                                        )
                                    }

                                    composable(Screen.StaffCheckIn.route) {
                                        StaffCheckInScreen(
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }

                                    composable(Screen.StaffSellTicket.route) {
                                        val sellVm: StaffSellTicketViewModel = viewModel()
                                        StaffSellTicketScreen(
                                            viewModel = sellVm,
                                            onNavigateToSeatSelection = { showtimeId, movieTitle, moviePosterUrl, cinemaName, roomName, startTime, basePrice ->
                                                navController.navigate(
                                                    Screen.StaffSellSeatSelection.createRoute(
                                                        showtimeId, movieTitle, moviePosterUrl,
                                                        cinemaName, roomName, startTime, basePrice
                                                    )
                                                )
                                            },
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = Screen.StaffSellSeatSelection.route,
                                        arguments = listOf(
                                            navArgument("showtimeId") { type = NavType.LongType },
                                            navArgument("movieTitle") { type = NavType.StringType },
                                            navArgument("moviePosterUrl") { type = NavType.StringType },
                                            navArgument("cinemaName") { type = NavType.StringType },
                                            navArgument("roomName") { type = NavType.StringType },
                                            navArgument("startTime") { type = NavType.StringType },
                                            navArgument("basePrice") { type = NavType.FloatType }
                                        )
                                    ) { entry ->
                                        val showtimeId = entry.arguments?.getLong("showtimeId") ?: 0L
                                        val movieTitle = android.net.Uri.decode(entry.arguments?.getString("movieTitle") ?: "")
                                        val moviePosterUrl = android.net.Uri.decode(entry.arguments?.getString("moviePosterUrl") ?: "")
                                        val cinemaName = android.net.Uri.decode(entry.arguments?.getString("cinemaName") ?: "")
                                        val roomName = android.net.Uri.decode(entry.arguments?.getString("roomName") ?: "")
                                        val startTime = android.net.Uri.decode(entry.arguments?.getString("startTime") ?: "")
                                        val basePrice = entry.arguments?.getFloat("basePrice")?.toDouble() ?: 0.0
                                        val sellVm: StaffSellTicketViewModel = viewModel(viewModelStoreOwner = entry)
                                        StaffSellSeatSelectionScreen(
                                            showtimeId = showtimeId,
                                            movieTitle = movieTitle,
                                            cinemaName = cinemaName,
                                            roomName = roomName,
                                            startTime = startTime,
                                            basePrice = basePrice,
                                            viewModel = sellVm,
                                            onProceedToPayment = { seatIds, total ->
                                                val seatIdsStr = seatIds.joinToString(",")
                                                val seatsDisplay = seatIds.size.toString() + " ghế"
                                                navController.navigate(
                                                    Screen.StaffSellPayment.createRoute(
                                                        showtimeId, seatIdsStr, seatsDisplay,
                                                        total, movieTitle, cinemaName, startTime
                                                    )
                                                )
                                            },
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }

                                    composable(
                                        route = Screen.StaffSellPayment.route,
                                        arguments = listOf(
                                            navArgument("showtimeId") { type = NavType.LongType },
                                            navArgument("seatIds") { type = NavType.StringType },
                                            navArgument("seatsDisplay") { type = NavType.StringType },
                                            navArgument("totalAmount") { type = NavType.FloatType },
                                            navArgument("movieTitle") { type = NavType.StringType },
                                            navArgument("cinemaName") { type = NavType.StringType },
                                            navArgument("startTime") { type = NavType.StringType }
                                        )
                                    ) { entry ->
                                        val showtimeId = entry.arguments?.getLong("showtimeId") ?: 0L
                                        val seatIdsStr = android.net.Uri.decode(entry.arguments?.getString("seatIds") ?: "")
                                        val seatsDisplay = android.net.Uri.decode(entry.arguments?.getString("seatsDisplay") ?: "")
                                        val total = entry.arguments?.getFloat("totalAmount")?.toDouble() ?: 0.0
                                        val movieTitle = android.net.Uri.decode(entry.arguments?.getString("movieTitle") ?: "")
                                        val cinemaName = android.net.Uri.decode(entry.arguments?.getString("cinemaName") ?: "")
                                        val startTime = android.net.Uri.decode(entry.arguments?.getString("startTime") ?: "")
                                        val seatIds = seatIdsStr.split(",").mapNotNull { it.toLongOrNull() }

                                        var showSuccess by remember { androidx.compose.runtime.mutableStateOf(false) }
                                        val sellVm: StaffSellTicketViewModel = viewModel(viewModelStoreOwner = entry)

                                        // Pre-set selected seats from route args
                                        LaunchedEffect(seatIds) {
                                            sellVm.selectedSeats.value = seatIds.toSet()
                                        }

                                        if (showSuccess) {
                                            StaffPaymentSuccessScreen(
                                                onSellAnother = {
                                                    navController.navigate(Screen.StaffSellTicket.route) {
                                                        popUpTo(Screen.StaffDashboard.route)
                                                    }
                                                },
                                                onGoHome = {
                                                    navController.navigate(Screen.StaffDashboard.route) {
                                                        popUpTo(Screen.StaffDashboard.route) { inclusive = true }
                                                    }
                                                }
                                            )
                                        } else {
                                            StaffSellPaymentScreen(
                                                showtimeId = showtimeId,
                                                seatIds = seatIds,
                                                seatsDisplay = seatsDisplay,
                                                totalAmount = total,
                                                movieTitle = movieTitle,
                                                cinemaName = cinemaName,
                                                startTime = startTime,
                                                viewModel = sellVm,
                                                onPaymentSuccess = { showSuccess = true },
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
        }
    }

    // Xử lý deep link khi app đang chạy và nhận được intent mới (VNPay/MoMo redirect)
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

fun NavController.navigateToTab(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateToTab.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Xử lý deep link từ VNPay/MoMo khi redirect về app.
 * VNPay trả về: cahcinema://payment/result?vnp_ResponseCode=00&vnp_TxnRef=...
 * MoMo trả về:  cahcinema://payment/result?resultCode=0&orderId=...
 *
 * Polling trong PaymentViewModel sẽ tự detect PAID status — hàm này chỉ cần
 * đưa user về đúng màn hình nếu app bị minimize.
 */
fun handlePaymentDeepLink(intent: android.content.Intent?, navController: NavController) {
    val uri = intent?.data ?: return
    if (uri.scheme != "cahcinema" || uri.host != "payment") return

    Log.d("DeepLink", "Payment redirect received: $uri")

    // VNPay: vnp_ResponseCode=00 là thành công
    val vnpCode = uri.getQueryParameter("vnp_ResponseCode")
    // MoMo: resultCode=0 là thành công
    val momoCode = uri.getQueryParameter("resultCode")

    val isSuccess = vnpCode == "00" || momoCode == "0"
    Log.d("DeepLink", "Payment result: ${if (isSuccess) "SUCCESS" else "FAILED"} (vnp=$vnpCode, momo=$momoCode)")

    // Polling trong PaymentViewModel tự xử lý việc cập nhật UI khi nhận PAID status.
    // Deep link chỉ bring app về foreground — không cần navigate thêm.
}
