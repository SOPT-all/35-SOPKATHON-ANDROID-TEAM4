package org.sopt.and4ever.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.sopt.and4ever.core.navigation.Route

@Composable
fun JPNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {

        }

        composable<Route.Input> {

        }

        composable<Route.Result> {

        }

        composable<Route.MyPing> {

        }

        composable<Route.MyPingDetail> {

        }
    }
}