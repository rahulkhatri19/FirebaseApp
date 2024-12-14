package com.geekforgeek.firebaseapp

import android.content.Context
import android.widget.Toast

object Utility {
    const val IS_EMAIL = "isEmail"
    const val IS_LOGIN = "isLogin"
    const val TRUE = "true"
    const val FALSE = "false"

    fun toastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}