package com.geekforgeek.firebaseapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FirebaseHome(navController: NavController) {

    Column {
        val auth = FirebaseAuth.getInstance()
        Spacer(Modifier.height(100.dp))

        IconButton(
            onClick = {
                auth.signOut()
                navController.popBackStack()
            }
        ) { }

        Text(
            text = "Home Screen"
        )
    }
}