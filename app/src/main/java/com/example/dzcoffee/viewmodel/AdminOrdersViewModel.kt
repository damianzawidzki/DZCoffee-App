package com.example.dzcoffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dzcoffee.data.model.Order
import com.example.dzcoffee.data.repository.OrderRepository
import kotlinx.coroutines.launch

// ViewModel for admin orders list
class AdminOrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _ordersState = MutableLiveData<ResultState<List<Order>>>()
    val ordersState: LiveData<ResultState<List<Order>>> = _ordersState

    private val _updateState = MutableLiveData<ResultState<Unit>>()
    val updateState: LiveData<ResultState<Unit>> = _updateState

    // Load all orders for admin panel
    fun loadAllOrders() {
        _ordersState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val list = orderRepository.getAllOrders()
                _ordersState.value = ResultState.Success(list)
            } catch (e: Exception) {
                _ordersState.value = ResultState.Error(e.message)
            }
        }
    }

    // Change status of one order
    fun updateStatus(orderId: String, newStatus: String) {
        _updateState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                orderRepository.updateOrderStatus(orderId, newStatus)
                _updateState.value = ResultState.Success(Unit)
                // reload list after status change
                loadAllOrders()
            } catch (e: Exception) {
                _updateState.value = ResultState.Error(e.message)
            }
        }
    }
}
