package com.geekforgeek.firebaseapp

import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.geekforgeek.firebaseapp.FireRoute.FIREBASE_EMAIL_PHONE
import com.geekforgeek.firebaseapp.FireRoute.FIREBASE_HOME
import com.geekforgeek.firebaseapp.SecretKey.WEB_CLIENT_KEY
import com.geekforgeek.firebaseapp.Utility.FALSE
import com.geekforgeek.firebaseapp.Utility.IS_EMAIL
import com.geekforgeek.firebaseapp.Utility.IS_LOGIN
import com.geekforgeek.firebaseapp.Utility.TRUE
import com.geekforgeek.firebaseapp.Utility.toastMessage
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

val TAG = "FirebaseLoginAuth"
@Composable
fun FirebaseLoginAuth(navController: NavController) {

    Column(modifier = Modifier.fillMaxWidth()) {

        val auth = FirebaseAuth.getInstance()

//        if(auth.currentUser != null) {
//            navController.popBackStack()
//            navController.navigate(FIREBASE_HOME)
//        }

        val context = LocalContext.current

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    toastMessage(context, "Google sign In Success")
                    Log.e(TAG, "Result : $result")
                }
            }
        )

        Spacer(Modifier.height(120.dp))
        Image(
            painter = painterResource(R.drawable.ic_instagram_logo),
            contentDescription = "Instagram Logo",
            modifier = Modifier.align(Alignment.CenterHorizontally).size(120.dp)
        )

        Card(
            modifier = Modifier
                .padding(end = 12.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            onClick = {
                callGoogleSignIn(context, launcher)
            }
        ) {
            Row(Modifier.padding(16.dp)) {
                Image(
                    painter = painterResource(R.drawable.ic_google_g),
                    contentDescription = "Instagram Logo",
                    modifier = Modifier.align(Alignment.CenterVertically).size(20.dp)
                )

                Text(
                    text = "Sign In with Google",
                    Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .padding(end = 12.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Red),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            onClick = {
                navController.navigate(route = "$FIREBASE_EMAIL_PHONE?$IS_EMAIL=$TRUE&$IS_LOGIN=$TRUE")

            }
        ) {
            Row(Modifier.padding(16.dp)) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Email Logo",
                    modifier = Modifier.align(Alignment.CenterVertically).size(20.dp)
                )

                Text(
                    text = "Sign In with Email ",
                    Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .padding(end = 12.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Cyan),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            onClick = {
                navController.navigate(route = "$FIREBASE_EMAIL_PHONE?$IS_EMAIL=$FALSE&$IS_LOGIN=$TRUE")
            }
        ) {
            Row(Modifier.padding(16.dp)) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Phone Logo",
                    modifier = Modifier.align(Alignment.CenterVertically).size(20.dp)
                )

                Text(
                    text = "Sign In with Phone ",
                    Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            modifier = Modifier.clickable {
                navController.navigate(route = "$FIREBASE_EMAIL_PHONE?$IS_EMAIL=$TRUE&$IS_LOGIN=$FALSE")
            }.
            align(Alignment.CenterHorizontally)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val verticalOffset = size.height - 2.dp.toPx()
                    drawLine(
                        color = Color.Blue,
                        strokeWidth = strokeWidth,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset)
                    )
                },
            text = "Don't have an Account? Sign Up",
            color = Color.Blue,
        )
    }
}


fun callGoogleSignIn(
    context: Context,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    GlobalScope.launch(Dispatchers.Default) {
        launcher.launch(
            IntentSenderRequest.Builder(
                signInWithGoogle(context)!!.pendingIntent.intentSender
            ).build()
        )
    }

}

suspend fun signInWithGoogle(context: Context): BeginSignInResult? {

    val beginSignInRequest = Identity.getSignInClient(context).beginSignIn(
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(WEB_CLIENT_KEY)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    ).await()
    return beginSignInRequest
}




