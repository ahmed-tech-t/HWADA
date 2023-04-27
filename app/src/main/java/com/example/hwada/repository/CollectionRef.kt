package com.example.hwada.repository

import com.example.hwada.Model.Ad
import com.example.hwada.Model.MyReview
import com.example.hwada.database.DbHandler
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class CollectionRef {

    companion object {
        fun getAdColHomePageRef(rootRef: FirebaseFirestore): CollectionReference {
            return getAdColRef(rootRef, DbHandler.homePage, DbHandler.homePage)
        }

        fun getAdColRef(rootRef: FirebaseFirestore, ad: Ad): CollectionReference {
            return if (ad.category == DbHandler.FREELANCE) {
                getAdColRef(rootRef, ad.category, ad.subCategory, ad.subSubCategory)
            } else {
                getAdColRef(rootRef, ad.category, ad.subCategory)
            }
        }
        fun userDocumentRef(rootRef: FirebaseFirestore , id: String): DocumentReference {
            return rootRef.collection(DbHandler.userCollection).document(id)
        }
        fun getAdColRef(rootRef: FirebaseFirestore, myReview: MyReview): CollectionReference {
            return if (myReview.category == DbHandler.FREELANCE) {
                getAdColRef(rootRef, myReview.category, myReview.subCategory, myReview.subSubCategory)
            } else {
                getAdColRef(rootRef, myReview.category, myReview.subCategory)
            }
        }

        private fun getAdColRef(
            rootRef: FirebaseFirestore,
            category: String,
            subCategory: String
        ): CollectionReference {
            return rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory)
        }

        private fun getAdColRef(
            rootRef: FirebaseFirestore,
            category: String,
            subCategory: String,
            subSubCategory: String
        ): CollectionReference {
            return rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(category)
                .document(subCategory)
                .collection(subSubCategory)
        }
         fun getChatRef(rootRef: FirebaseFirestore,id: String): CollectionReference {
            return rootRef.collection(DbHandler.userCollection).document(id)
                .collection(DbHandler.chatCollection)
        }
    }


}
