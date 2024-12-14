package com.geekforgeek.firebaseapp

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.geekforgeek.firebaseapp.FireRoute.FIREBASE_HOME
import com.geekforgeek.firebaseapp.Utility.toastMessage
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

val TAG_Email = "FirebaseEmailPhone"

@Composable
fun FirebaseEmailPhone(navController: NavController, isEmail: Boolean, isLogin: Boolean) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        val context = LocalContext.current
        val auth = FirebaseAuth.getInstance()

        var inputEmail by remember { mutableStateOf(TextFieldValue("")) }
        var inputPassword by remember { mutableStateOf(TextFieldValue("")) }

        val emailLabel = if (isEmail) "Email" else "Phone"
        val passwordLabel = if (isEmail) "Password" else "OTP"
        val emailKeyboardType = if (isEmail) KeyboardType.Email else KeyboardType.Phone
        val passwordKeyboardType =
            if (isEmail) KeyboardType.Password else KeyboardType.NumberPassword

        val loginButton = if (isLogin) "Log In" else "Sign Up"
        Spacer(Modifier.height(20.dp))
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, "")
        }

        Spacer(Modifier.height(120.dp))
        Image(
            painter = painterResource(R.drawable.ic_instagram_logo),
            contentDescription = "Instagram Logo",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp)
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputEmail,
            onValueChange = {
                inputEmail = it
            },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            label = {
                Text(
                    text = emailLabel,
                    fontSize = 16.sp
                )
            },
            placeholder = {
                Text(
                    text = emailLabel,
                    fontSize = 16.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = emailKeyboardType)
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputPassword,
            onValueChange = {
                inputPassword = it
            },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            label = {
                Text(
                    text = passwordLabel,
                    fontSize = 16.sp
                )
            },
            placeholder = {
                Text(
                    text = passwordLabel,
                    fontSize = 16.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = passwordKeyboardType)
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (isLogin) {
                    callPhoneEmail(
                        isEmail,
                        auth,
                        inputEmail.text,
                        inputPassword.text,
                        context,
                        navController
                    )
                } else {
                    auth.createUserWithEmailAndPassword(inputEmail.text, inputPassword.text)
                        .addOnCompleteListener(context as Activity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG_Email, "signUpWithEmail:success, auth:$auth")
                                toastMessage(context, "Sign up with Email Success")
                                navController.navigate(FIREBASE_HOME)
                                val user = auth.currentUser
                            } else {
                                // If sign in fails, display a message to the user.
                                toastMessage(context, "Sign Up Failed : ${task.exception}")
                                Log.w(TAG_Email, "signUpWithEmail:failure", task.exception)
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = loginButton
            )
        }
    }

}

fun callPhoneEmail(
    isEmail: Boolean,
    auth: FirebaseAuth,
    email: String,
    password: String,
    context: Context,
    navController: NavController
) {

    if (isEmail) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success, auth$auth")
                    toastMessage(context, "Sign In with Email success")
                    navController.navigate(FIREBASE_HOME)
                    val user = auth.currentUser
                    //    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    toastMessage(context, "Sign In with Email Fail: ${task.exception}")
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }
    } else {
        callPhoneLogin(auth, email, password, context, navController)
    }
}

fun callPhoneLogin(
    auth: FirebaseAuth,
    phone: String,
    otp: String,
    context: Context,
    navController: NavController
) {

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            toastMessage(context, "Sign In with Phone Verification complete")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            toastMessage(context, "Phone Verification fail: ${e.message}")
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                toastMessage(context, "Phone Verification fail : Invalid request")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                toastMessage(context, "Phone Verification fail : SMS quota exceeded")
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
                toastMessage(context, "Phone Verification fail : reCAPTCHA")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(context as Activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        toastMessage(context, "Phone SignIn success")
                        Log.d(TAG, "signInWithCredential:success, auth${auth.currentUser}")
                        navController.navigate(FIREBASE_HOME)
                        val user = auth.currentUser
                        //    updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        toastMessage(context, "Phone sign in fail: ${task.exception}")
                        Log.w(TAG, "signInWithCredential:failure", task.exception)

                        //   updateUI(null)
                    }

                }
        }
    }

    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phone) // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(context as Activity) // Activity (for callback binding)
        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}
