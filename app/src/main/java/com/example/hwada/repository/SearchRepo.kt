package com.example.hwada.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hwada.Model.Ad
import com.example.hwada.Model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepo @Inject constructor(
    val  rootRef : FirebaseFirestore
) {

    private val TAG = "SearchRepo"

    suspend fun search(user: User, keyword : String ): Flow<List<Ad>> = callbackFlow {
        CollectionRef.getAdColHomePageRef(rootRef)
            .whereEqualTo("active", true)
            .whereEqualTo("title",keyword)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val ads = ArrayList<Ad>()
                    val adQuerySnapshot = task.result
                    for (adSnapshot in adQuerySnapshot) {
                        val ad = adSnapshot.toObject(Ad::class.java)
                        ad.setDistance_(user.location)
                        ads.add(ad)
                    }
                    trySend(ads)
                } else {
                    Log.e(TAG, "onComplete: error loading ads ")
                    trySend(ArrayList())
                }
                close() // close the channel when the operation is complete
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                close(e) // close the channel with an error if the operation fails
            }
        awaitClose()
    }.flowOn(Dispatchers.IO)

}