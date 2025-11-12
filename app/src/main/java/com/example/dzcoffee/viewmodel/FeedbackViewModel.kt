package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzcoffee.data.model.Feedback
import com.example.dzcoffee.data.repository.FeedbackRepository
import kotlinx.coroutines.launch

// ViewModel for feedback dialog on orders
class FeedbackViewModel(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _sendState = MutableLiveData<ResultState<Unit>>()
    val sendState: LiveData<ResultState<Unit>> = _sendState

    fun sendFeedback(orderId: String, userId: String, rating: Int, comment: String) {
        if (rating <= 0) {
            _sendState.value = ResultState.Error("Please select rating")
            return
        }

        val feedback = Feedback(
            orderId = orderId,
            userId = userId,
            rating = rating,
            comment = comment
        )

        _sendState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                feedbackRepository.sendFeedback(feedback)
                _sendState.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _sendState.value = ResultState.Error(e.message)
            }
        }
    }

    fun reset() {
        _sendState.value = ResultState.Idle
    }
}
