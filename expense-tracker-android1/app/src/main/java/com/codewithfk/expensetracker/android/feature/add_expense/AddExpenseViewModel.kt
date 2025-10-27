package com.codewithfk.expensetracker.android.feature.add_expense

import androidx.lifecycle.viewModelScope
import com.codewithfk.expensetracker.android.base.AddExpenseNavigationEvent
import com.codewithfk.expensetracker.android.base.BaseViewModel
import com.codewithfk.expensetracker.android.base.NavigationEvent
import com.codewithfk.expensetracker.android.base.UiEvent
import com.codewithfk.expensetracker.android.data.dao.ExpenseDao
import com.codewithfk.expensetracker.android.data.model.ExpenseEntity
import com.codewithfk.expensetracker.android.data.remote.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val dao: ExpenseDao
) : BaseViewModel() {

    private val firestoreRepository = FirestoreRepository()

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            dao.insertExpense(expenseEntity)
            firestoreRepository.addExpense(expenseEntity)
            true
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddExpenseUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = addExpense(event.expenseEntity)
                        if (result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddExpenseUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }

            is AddExpenseUiEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(AddExpenseNavigationEvent.MenuOpenedClicked)
                }
            }
        }
    }
}

sealed class AddExpenseUiEvent : UiEvent() {
    data class OnAddExpenseClicked(val expenseEntity: ExpenseEntity) : AddExpenseUiEvent()
    object OnBackPressed : AddExpenseUiEvent()
    object OnMenuClicked : AddExpenseUiEvent()
}
