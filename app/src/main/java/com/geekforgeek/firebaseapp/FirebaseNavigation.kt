package com.geekforgeek.firebaseapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.geekforgeek.firebaseapp.FireRoute.FIREBASE_EMAIL_PHONE
import com.geekforgeek.firebaseapp.FireRoute.FIREBASE_HOME
import com.geekforgeek.firebaseapp.FireRoute.FIREBASE_LOGIN_AUTH
import com.geekforgeek.firebaseapp.Utility.IS_EMAIL
import com.geekforgeek.firebaseapp.Utility.IS_LOGIN

@Composable
fun FirebaseNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = FIREBASE_LOGIN_AUTH) {
        composable(FIREBASE_LOGIN_AUTH) {
            FirebaseLoginAuth(navController = navController)
        }
        composable(
            route = "$FIREBASE_EMAIL_PHONE?$IS_EMAIL={$IS_EMAIL}&$IS_LOGIN={$IS_LOGIN}",
            arguments = listOf(
                navArgument(
                    name = IS_EMAIL
                ){
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(
                    name = IS_LOGIN
                ){
                    type = NavType.BoolType
                    defaultValue = false
                },
            )
        ) {
            val isEmail = it.arguments?.getBoolean(IS_EMAIL, false) ?: false
            val isLogin = it.arguments?.getBoolean(IS_LOGIN, false) ?: false
            FirebaseEmailPhone(navController = navController, isEmail, isLogin)
        }
        composable(FIREBASE_HOME) {
            FirebaseHome(navController = navController)
        }
    }

}

object FireRoute {
    const val FIREBASE_HOME = "firebase_home"
    const val FIREBASE_LOGIN_AUTH = "firebase_login_auth"
    const val FIREBASE_EMAIL_PHONE = "firebase_email_phone"
}