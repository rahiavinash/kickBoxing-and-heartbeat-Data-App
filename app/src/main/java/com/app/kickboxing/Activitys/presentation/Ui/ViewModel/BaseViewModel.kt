package com.app.kickboxing.Activitys.presentation.Ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kickboxing.Activitys.presentation.state.UIAction
import com.app.kickboxing.Activitys.presentation.state.UIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BaseViewModel<S : UIState, A : UIAction> : ViewModel() {
    private val _uiState = MutableStateFlow<S?>(null)
    val uiState: StateFlow<S?> = _uiState

    private val _uiAction = MutableSharedFlow<A>()
    val uiAction: SharedFlow<A> = _uiAction

    fun setState(state: S) {
        _uiState.value = state
    }

    fun sendAction(action: A) {
        viewModelScope.launch {
            _uiAction.emit(action)
        }
    }
}