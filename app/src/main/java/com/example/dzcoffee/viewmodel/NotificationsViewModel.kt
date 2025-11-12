package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzcoffee.data.model.NotificationItem
import com.example.dzcoffee.data.repository.NotificationRepository
import kotlinx.coroutines.launch

// ViewModel for Notifications screen
class NotificationsViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notificationsState = MutableLiveData<ResultState<List<NotificationItem>>>()
    val notificationsState: LiveData<ResultState<List<NotificationItem>>> = _notificationsState

    private val _markReadState = MutableLiveData<ResultState<Unit>>()
    val markReadState: LiveData<ResultState<Unit>> = _markReadState

    // Load all notifications for user
    fun loadNotifications(userId: String) {
        _notificationsState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val list = notificationRepository.getNotifications(userId)
                _notificationsState.value = ResultState.Success(list)
            } catch (e: Exception) {
                _notificationsState.value = ResultState.Error(e.message)
            }
        }
    }

    // Mark all as read
    fun markAllRead(userId: String) {
        _markReadState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                notificationRepository.markAllRead(userId)
                _markReadState.value = ResultState.Success(Unit)
                loadNotifications(userId)
            } catch (e: Exception) {
                _markReadState.value = ResultState.Error(e.message)
            }
        }
    }
}
