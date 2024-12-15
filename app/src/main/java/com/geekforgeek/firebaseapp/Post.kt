package com.geekforgeek.firebaseapp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val userImage: String? = null,
    val postImage: String? = null,
    val userName: String? = null,
    val like: String? = null
)
