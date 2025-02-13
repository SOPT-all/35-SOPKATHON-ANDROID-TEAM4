package org.sopt.and4ever.presentation.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.sopt.and4ever.core.navigation.BottomNavigationBar
import org.sopt.and4ever.core.navigation.BottomNavigationItem
import org.sopt.and4ever.core.navigation.Route
import org.sopt.and4ever.data.service.MyPingDetailService
import org.sopt.and4ever.data.service.MyPingService
import org.sopt.and4ever.data.service.OtherPingService
import org.sopt.and4ever.data.service.PingService
import org.sopt.and4ever.presentation.home.HomeScreen
import org.sopt.and4ever.presentation.input.InputScreen
import org.sopt.and4ever.presentation.myping.MyPingScreen
import org.sopt.and4ever.presentation.mypingdetail.MyPingDetailScreen
import org.sopt.and4ever.presentation.mypingdetail.MyPingDetailSnackBar
import org.sopt.and4ever.presentation.otherping.OtherPingScreen
import org.sopt.and4ever.presentation.result.ResultScreen

@Composable
fun JPNavigation(
    myPingService: MyPingService,
    otherPingService: OtherPingService,
    pingService: PingService,
    myPingDetailService: MyPingDetailService,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    var selectedMainBottomTab by remember { mutableStateOf(BottomNavigationItem.HOME) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember { derivedStateOf { navBackStackEntry?.destination?.route } }

    val snackbarHostState = remember{ SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.systemBarsPadding(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ){ MyPingDetailSnackBar() }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedMainBottomTab,
                onItemSelected = { selectedMainBottomTab = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {
        Surface(
            color = Color.White
        ) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = Route.Home,
                enterTransition = {
                    EnterTransition.None
                }, exitTransition = {
                    ExitTransition.None
                }
            ) {
                composable<Route.Home> {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToInputScreen = {
                            navController.navigate(Route.Input)
                        },
                    )
                }

                composable<Route.Input> {
                    InputScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateWithInput = { situation ->
                            navController.navigate(Route.Result(situation))
                        }
                    )
                }

                composable<Route.Result> {
                    ResultScreen(
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToMyPingScreen = {
                            navController.navigate(Route.MyPing)
                        },
                        pingService = pingService,
                        situation = it.toRoute<Route.Result>().situation
                    )
                }

                composable<Route.MyPing> {
                    MyPingScreen(
                        myPingService = myPingService,
                        onNavigateToMyPingDetail = {
                            navController.navigate(Route.MyPingDetail(it))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 41.dp)
                    )
                }

                composable<Route.MyPingDetail> {
                    val id = it.toRoute<Route.MyPingDetail>().id
                    MyPingDetailScreen(
                        myPingDetailService = myPingDetailService,
                        myPingId = id,
                        modifier = Modifier.fillMaxSize(),
                        popUp = {
                            navController.navigate(Route.MyPing) {
                                popUpTo(Route.MyPing) {
                                    inclusive = true
                                }
                            }
                        },
                        navigateToMyPing = { navController.navigate(Route.MyPing) },
                        snackbarState = snackbarHostState,
                        coroutineScope = coroutineScope
                    )
                }

                composable<Route.OtherPing> {
                    OtherPingScreen(
                        otherPingService = otherPingService,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        LaunchedEffect(key1 = selectedMainBottomTab) {  // 하단 탭 선택에 의한 라우팅 처리
            val targetRoute = when (selectedMainBottomTab) {
                BottomNavigationItem.HOME -> Route.Home::class.qualifiedName
                BottomNavigationItem.MY_PING -> Route.MyPing::class.qualifiedName
                BottomNavigationItem.OTHER_PING -> Route.OtherPing::class.qualifiedName
            } ?: ""

            navController.navigate(targetRoute) {
                popUpTo(Route.Home) {
                    saveState = true
                    inclusive = false
                }
                launchSingleTop = true
            }
        }

        LaunchedEffect(key1 = currentRoute) {   // 뒤로가기에 의한 하단 탭 변경 처리
            selectedMainBottomTab = when (currentRoute) {
                Route.Home::class.qualifiedName -> BottomNavigationItem.HOME
                Route.MyPing::class.qualifiedName -> BottomNavigationItem.MY_PING
                Route.OtherPing::class.qualifiedName -> BottomNavigationItem.OTHER_PING
                else -> selectedMainBottomTab
            }
        }
    }
}