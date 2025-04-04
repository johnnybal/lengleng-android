package com.example.lengleng.services

import com.example.lengleng.models.Poll
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PollService {
    private val db = FirebaseFirestore.getInstance()
    private val pollsCollection = db.collection("polls")

    suspend fun createPoll(poll: Poll): Result<Poll> {
        return try {
            val pollRef = pollsCollection.document(poll.id)
            pollRef.set(poll).await()
            Result.success(poll)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPoll(pollId: String): Result<Poll> {
        return try {
            val snapshot = pollsCollection.document(pollId).get().await()
            val poll = snapshot.toObject(Poll::class.java)
            if (poll != null) {
                Result.success(poll)
            } else {
                Result.failure(Exception("Poll not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePoll(pollId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            pollsCollection.document(pollId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePoll(pollId: String): Result<Unit> {
        return try {
            pollsCollection.document(pollId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPollsByUser(userId: String): Result<List<Poll>> {
        return try {
            val snapshot = pollsCollection
                .whereEqualTo("creatorId", userId)
                .get()
                .await()
            
            val polls = snapshot.toObjects(Poll::class.java)
            Result.success(polls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentPolls(limit: Int = 20): Result<List<Poll>> {
        return try {
            val snapshot = pollsCollection
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val polls = snapshot.toObjects(Poll::class.java)
            Result.success(polls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun voteOnPoll(pollId: String, userId: String, optionId: String): Result<Unit> {
        return try {
            val pollRef = pollsCollection.document(pollId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(pollRef)
                val poll = snapshot.toObject(Poll::class.java)
                
                if (poll != null) {
                    val updatedVotes = poll.votes.toMutableList()
                    updatedVotes.add(com.example.lengleng.models.Vote(userId, optionId))
                    
                    val updatedOptions = poll.options.map { option ->
                        if (option.id == optionId) {
                            option.copy(votes = option.votes + 1)
                        } else {
                            option
                        }
                    }
                    
                    transaction.update(pollRef, mapOf(
                        "votes" to updatedVotes,
                        "options" to updatedOptions
                    ))
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(pollId: String, comment: com.example.lengleng.models.Comment): Result<Unit> {
        return try {
            val pollRef = pollsCollection.document(pollId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(pollRef)
                val poll = snapshot.toObject(Poll::class.java)
                
                if (poll != null) {
                    val updatedComments = poll.comments.toMutableList()
                    updatedComments.add(comment)
                    
                    transaction.update(pollRef, "comments", updatedComments)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 