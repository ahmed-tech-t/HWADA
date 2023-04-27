package com.example.hwada.repository

import com.example.hwada.Model.Ad
import com.example.hwada.Model.AdReview
import com.example.hwada.Model.Chat
import com.example.hwada.Model.User
import com.example.hwada.database.DbHandler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.ArrayList
import javax.inject.Inject

class NotificationRepo @Inject constructor(
    var rootRef:FirebaseFirestore
    ) {
    private val TAG = "Notification"
    val ioScope = CoroutineScope(Dispatchers.IO)

    suspend fun chatListener(userId: String): Flow<Chat> = callbackFlow {
            val querySnapshot = CollectionRef.getChatRef(rootRef, userId)
                .orderBy("lastMessage.timeStamp", Query.Direction.ASCENDING)
            val listener = querySnapshot.addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                   ioScope.launch {
                        querySnapshot?.documents?.forEach { doc ->
                            val chat = doc.toObject(Chat::class.java)!!
                            val adTask = CollectionRef.getAdColRef(rootRef, chat.ad).document(chat.ad.id).get().await()
                            val userTask = CollectionRef.userDocumentRef(rootRef,chat.receiver.uId).get().await()
                            chat.ad = adTask.toObject(Ad::class.java)!!
                            chat.receiver = userTask.toObject(User::class.java)!!
                            trySend(chat)
                        }
                    }
                }
            awaitClose { listener.remove() }
        }.flowOn(Dispatchers.IO)

    suspend fun myAdsListener(userId:String):Flow<Ad> = callbackFlow {
        val querySnapshot = CollectionRef.userDocumentRef(rootRef,userId).collection(DbHandler.adCollection)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
        val listener = querySnapshot.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            ioScope.launch {
                querySnapshot?.documents?.forEach { doc ->
                    val ad = doc.toObject(Ad::class.java)!!
                    val reviewsTask = doc.reference.collection(DbHandler.Reviews).get().await()
                    ad.adReviews = reviewsTask.toObjects(AdReview::class.java) as ArrayList<AdReview>?
                    trySend(ad)
                 }
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
}