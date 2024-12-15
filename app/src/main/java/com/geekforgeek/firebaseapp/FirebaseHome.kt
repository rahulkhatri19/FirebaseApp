package com.geekforgeek.firebaseapp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.geekforgeek.firebaseapp.Utility.toastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun FirebaseHome(navController: NavController) {

    val firebaseDatabase = Firebase.database.reference
    val context = LocalContext.current

    var postList by remember { mutableStateOf(mutableStateListOf<Post>()) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 56.dp)) {
        val auth = FirebaseAuth.getInstance()
        Spacer(Modifier.height(20.dp))

        IconButton(
            onClick = {
                auth.signOut()
                navController.popBackStack()
            }
        ) {

        }

        val referance = firebaseDatabase.child("users-1234").child("post")
        referance.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val post = it.getValue<Post>()
                    postList.add(post!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toastMessage(context, "Unable to fetch Data: ${error.message}")
            }

        })

        // getDataFromFirebase(firebaseDatabase, context)

        LazyColumn {
            items(postList) { list ->
                InstagramPost(list)
            }
        }


//        Button(onClick = {
//            createPost(firebaseDatabase)
//        }) {
//
//            Text("Create Post")
//        }
    }
}

fun getDataFromFirebase(firebaseDatabase: DatabaseReference, context: Context) {
    val referance = firebaseDatabase.child("users-1234").child("post")
    referance.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach {
                val post = it.getValue<Post>()

            }
        }

        override fun onCancelled(error: DatabaseError) {
            toastMessage(context, "Unable to fetch Data: ${error.message}")
        }

    })
}

fun createPost(firebaseDatabase: DatabaseReference) {



    val post = Post(
        userImage = "https://randomuser.me/api/portraits/med/women/90.jpg",
        postImage = "https://fastly.picsum.photos/id/17/2500/1667.jpg?hmac=HD-JrnNUZjFiP2UZQvWcKrgLoC_pc_ouUSWv8kHsJJY",
        userName = "Nurdan",
        like = "20"
    )

    firebaseDatabase.child("users-1234").child("post").push().setValue(post)

}

@Composable
fun InstagramPost(list: Post) {
    Column(Modifier.fillMaxWidth()) {
        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(list.userImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .padding(12.dp)
            )
            Text(
                text = list.userName ?: "",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(list.postImage)
                .crossfade(true)
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(height = 240.dp, width = 420.dp)
                .fillMaxWidth()
                .align(
                    Alignment.CenterHorizontally
                )
        )
    }
    Row(Modifier.padding(top = 12.dp, start = 12.dp)) {
        Icon(
            Icons.Default.FavoriteBorder, ""
        )
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_commet), modifier = Modifier.size(18.dp),
            contentDescription = ""
        )
        Spacer(Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_share), "",
            modifier = Modifier.size(18.dp)
        )
    }

    Text(
        "${list.like} likes",
        modifier = Modifier.padding(start = 12.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    Text("View all 20 comments", modifier = Modifier.padding(start = 12.dp), fontSize = 11.sp)
    Text("1 min ago", modifier = Modifier.padding(start = 12.dp), fontSize = 10.sp)
    Divider(thickness = 1.dp, color = Color.Gray)


}
