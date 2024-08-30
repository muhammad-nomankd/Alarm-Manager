package com.example.durranitech.repositories

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavController
import com.example.alarmmanager.R
import com.example.durranitech.dataclasses.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    fun SignIn(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        context: Context,
        navController: NavController
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(
                        id = auth.currentUser?.uid ?: "",
                        email = auth.currentUser?.email ?: "",
                        name = auth.currentUser?.displayName ?: "",
                        imageUrl = ""
                    )
                    val firestore = FirebaseFirestore.getInstance().collection("User")
                    firestore.document(auth.currentUser?.uid!!).set(user)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                onSuccess("You are successfully registered")
                            } else {
                                onError("Something went wrong please try again.")
                            }

                        }.addOnFailureListener {
                            onError("Error saving user data")
                        }
                }

            }.addOnFailureListener {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            onSuccess("You are successfully signed In")
                        } else {
                            onError("Error signing In")
                        }
                    }
            }
    }


    fun initGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun launchGoogleSignIn(activityResultLauncher: ActivityResultLauncher<Intent>) {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInClient = googleSignInClient.signInIntent
            activityResultLauncher.launch(signInClient)
        }
    }

    fun handleGoogleSignInResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account, onSuccess, onError)
        } catch (e: ApiException) {
            Log.d("api exception", e.message.toString())
        }
    }

    fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val firestore = FirebaseFirestore.getInstance().collection("User")
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credentials)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = User(
                        id = auth.currentUser?.uid ?: "",
                        email = account.email ?: "",
                        name = account.displayName ?: "",
                        imageUrl = account.photoUrl.toString()
                    )
                    firestore.document(auth.currentUser?.uid!!).set(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onSuccess()
                            } else {
                                onError("Error saving User data")
                            }
                        }
                } else {
                    onError("Something went wrong please try again.")
                }
            }
    }
}