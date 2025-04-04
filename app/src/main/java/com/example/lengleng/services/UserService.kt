package com.example.lengleng.services

import com.example.lengleng.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun getUser(userId: String): Result<User> = try {
        val document = firestore.collection("users").document(userId).get().await()
        if (document.exists()) {
            Result.success(document.toObject(User::class.java)!!)
        } else {
            Result.failure(Exception("User not found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateUser(user: User): Result<Unit> = try {
        firestore.collection("users").document(user.id).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signIn(email: String, password: String): Result<Unit> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signUp(email: String, password: String, username: String): Result<Unit> = try {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = User(
            id = authResult.user?.uid ?: throw Exception("User creation failed"),
            email = email,
            username = username
        )
        firestore.collection("users").document(user.id).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateUserStatus(userId: String, isOnline: Boolean): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update(mapOf(
                    "isOnline" to isOnline,
                    "lastActive" to System.currentTimeMillis()
                ))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 