package pe.edu.upc.careconnect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.edu.upc.careconnect.presentation.login.LoginScreen
import pe.edu.upc.careconnect.presentation.login.RegisterScreen
import pe.edu.upc.careconnect.presentation.onboarding.OnBoarding

@Composable
fun RootNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OnBoardingRoute
    ) {
        composable<OnBoardingRoute> {
            OnBoarding(
                onLoginClick = {
                    navController.navigate(LoginRoute)
                },
                onRegisterClick = {
                    navController.navigate(RegisterRoute)
                }
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(HomeRoute)
                },
                onCreateAccountClick = {
                    navController.navigate(RegisterRoute)
                }
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                onBackClick = {
                    navController.navigate(LoginRoute) {
                        popUpTo(RegisterRoute) {
                            inclusive = true
                        }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(HomeRoute)
                },
                onLoginClick = {
                    navController.navigate(LoginRoute) {
                        popUpTo(RegisterRoute) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<HomeRoute> {
            Main()
        }
    }
}