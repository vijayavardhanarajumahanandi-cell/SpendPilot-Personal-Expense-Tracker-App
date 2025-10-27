package com.codewithfk.expensetracker.android.data.remote

import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("expenses")

    suspend fun addExpense(expenseEntity: ExpenseEntity) {
        val data = hashMapOf(
            "title" to expenseEntity.title,
            "amount" to expenseEntity.amount,
            "date" to expenseEntity.date,
            "type" to expenseEntity.type
        )
        collectionRef.add(data).await()
    }
}
