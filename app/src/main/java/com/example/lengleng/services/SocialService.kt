package com.example.lengleng.services

import com.example.lengleng.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SocialService {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Result<Unit> {
        return try {
            val fromUserRef = usersCollection.document(fromUserId)
            val toUserRef = usersCollection.document(toUserId)
            
            db.runTransaction { transaction ->
                val fromUser = transaction.get(fromUserRef).toObject(User::class.java)
                val toUser = transaction.get(toUserRef).toObject(User::class.java)
                
                if (fromUser != null && toUser != null) {
                    val updatedPendingRequests = toUser.settings.privacy.allowFriendRequests
                    if (updatedPendingRequests) {
                        transaction.update(fromUserRef, "pendingRequests", 
                            com.google.firebase.firestore.FieldValue.arrayUnion(toUserId))
                        transaction.update(toUserRef, "pendingRequests", 
                            com.google.firebase.firestore.FieldValue.arrayUnion(fromUserId))
                    }
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptFriendRequest(userId: String, friendId: String): Result<Unit> {
        return try {
            val userRef = usersCollection.document(userId)
            val friendRef = usersCollection.document(friendId)
            
            db.runTransaction { transaction ->
                transaction.update(userRef, mapOf(
                    "connections" to com.google.firebase.firestore.FieldValue.arrayUnion(friendId),
                    "pendingRequests" to com.google.firebase.firestore.FieldValue.arrayRemove(friendId)
                ))
                transaction.update(friendRef, mapOf(
                    "connections" to com.google.firebase.firestore.FieldValue.arrayUnion(userId),
                    "pendingRequests" to com.google.firebase.firestore.FieldValue.arrayRemove(userId)
                ))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectFriendRequest(userId: String, friendId: String): Result<Unit> {
        return try {
            val userRef = usersCollection.document(userId)
            val friendRef = usersCollection.document(friendId)
            
            db.runTransaction { transaction ->
                transaction.update(userRef, "pendingRequests", 
                    com.google.firebase.firestore.FieldValue.arrayRemove(friendId))
                transaction.update(friendRef, "pendingRequests", 
                    com.google.firebase.firestore.FieldValue.arrayRemove(userId))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFriend(userId: String, friendId: String): Result<Unit> {
        return try {
            val userRef = usersCollection.document(userId)
            val friendRef = usersCollection.document(friendId)
            
            db.runTransaction { transaction ->
                transaction.update(userRef, "connections", 
                    com.google.firebase.firestore.FieldValue.arrayRemove(friendId))
                transaction.update(friendRef, "connections", 
                    com.google.firebase.firestore.FieldValue.arrayRemove(userId))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriends(userId: String): Result<List<User>> {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            
            if (user != null) {
                val friendsSnapshot = usersCollection
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), user.connections)
                    .get()
                    .await()
                
                val friends = friendsSnapshot.toObjects(User::class.java)
                Result.success(friends)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingRequests(userId: String): Result<List<User>> {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
            
            if (user != null) {
                val pendingSnapshot = usersCollection
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), user.settings.privacy.pendingRequests)
                    .get()
                    .await()
                
                val pendingUsers = pendingSnapshot.toObjects(User::class.java)
                Result.success(pendingUsers)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 