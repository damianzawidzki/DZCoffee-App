package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzcoffee.data.model.Order
import com.example.dzcoffee.data.repository.OrderRepository
import kotlinx.coroutines.launch

// ViewModel for customer "My Orders" screen
class OrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _ordersState = MutableLiveData<ResultState<List<Order>>>()
    val ordersState: LiveData<ResultState<List<Order>>> = _ordersState

    // Load orders for given user
    fun loadOrders(userId: String) {
        _ordersState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val list = orderRepository.getOrdersForUser(userId)
                _ordersState.value = ResultState.Success(list)
            } catch (e: Exception) {
                _ordersState.value = ResultState.Error(e.message)
            }
        }
    }
}
